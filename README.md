# STAR
### 开发背景
&emsp;&emsp;STAR框架旨在为解决微服务中的数据最终一致性问题，由于目前开源的一些分布式事务一致性框架大多是采用中心协调者的方式，而中心协调者需要需要控制所有分
支事务的提交和回滚工作，具有严重的单点问题和堵塞问题，它的性能和稳定性直接影响到了整个业务应用的性能和稳定性，一旦中心协调者出现故障，就会使得整个
业务应用无法正常运行。所以依据Saga模型设计与实现了一个基于事件驱动的、去中心化的分布式事务一致性框架——STAR。
<br>&emsp;&emsp;同时，虽然也有框架,例如Axon Framework,提供了基于事件驱动的Saga模型的实现，但是限定了整个框架的整体架构，框架与业务应用耦合性非常之高，使用时需
要重构业务应用的整体架构，极其不便。于是STAR框架利用Spring AOP技术，使得在业务架构较为复杂的前提下，如果想要引入，技术人员也不需要对原本的系统结
构做任何改动，避免因重构代码引发其他不必要的错误。
<br>&emsp;&emsp;除此之外，传统的Saga模型不能保证隔离性，仅能达到读未提交的隔离级别，这样在两个全局事务同时操作同一个数据库资源时，就会引发数据语义的不一致、更新
丢失、脏数据读取等问题，也就使得传统的Saga模型适用的业务场景有限。于是STAR框架对其进行了改进，利用全局写排它锁使Saga模型可以达到读已提交的隔离
级别，即可以达到两种不同的隔离级别，读未提交与读已提交，不仅可以适用于各全局事务之间不会操作共同资源的业务场景，也可以适用于两个全局事务需要对同一
个资源进行操作的场景。

### 整体架构
![avatar](https://github.com/star-saga/star/blob/master/picture/star-framework.png)
<br><br>&emsp;&emsp;STAR框架具有一个内嵌在每个微服务中的代理，称之为“Light”，它执行的工作主要为：
* 通过Spring AOP拦截每个服务的业务方法，在方法执行前后做增强处理，主要是幂等性保证、空回滚防止等，同时拦截发出的服务调用请求或者事件发布与订阅接口，插入事务上下文；
* 判断业务逻辑是否正常执行或是否执行超时，根据执行结果生成相应事件并发布至消息队列，同时订阅消息队列中相关事件，方便后续进行所有分支事务的依次提交或依次回滚；
* 消费到提交或回滚事件时利用反射调用执行业务方法的提交与回滚操作，并生成相应事件发布至消息队列以供下游服务进行消费。
* 如果需要达到读已提交的隔离级别，那么Light模块还需要通过对JDBC数据源的代理获取SQL语句并进行解析，将此次全局事务涉及到的数据库资源生成全局写排他锁并存储在Redis中，在全局事务执行结束之后再将全局锁释放，其他全局事务执行之前检查是否存在锁冲突，如果存在，则无法获取全局锁，这样可以在该全局事务执行期间防止其他全局事务对相关数据进行修改。

### 使用简介
&emsp;&emsp;在使用STAR框架时，由于框架涉及到与MySQL数据库、Redis数据库、Kafka消息队列进行交互，需要事先安装好这些软件，并处于开启状态。
同时，业务应用各服务需要添加以下依赖：
```
<dependency>
    <groupId>org.event.driven.saga</groupId>
    <artifactId>lightcommon</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.event.driven.saga</groupId>
    <artifactId>lightdatasource</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
#### 业务发起方
&emsp;&emsp;对于业务发起方，即Backend服务，只需要在调用方法上方添加@StartEvent注解即可
```
public class BackendService {
    ...

    @StartEvent
    public String start(OrderDetails orderDetails) {
        ...
    }
}
```
&emsp;&emsp;配置文件application.properties添加
```
publish.approve.topic = none
publish.reject.topic = none

subscribe.approve.topic = approveOrder
subscribe.reject.topic = rejectOrder
```

#### 服务提供者
&emsp;&emsp;对于服务提供者，即Order服务于Customer服务，这里以Order服务为例，需要在Config文件中添加Druid依赖
```
public class DruidConfiguration {
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DruidDataSource druidDataSource(){
        return new DruidDataSource();
    }

    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    @Bean("dataSource")
    public DataSourceProxy dataSourceProxy(DruidDataSource druidDataSource, LightContext lightContext) {
        DataSourceProxy dataSourceProxy = new DataSourceProxy(druidDataSource, lightContext);
        return dataSourceProxy;
    }
}
```
&emsp;&emsp;在调用方法上方添加@CreateEvent注解
```
public class OrderService {
    ...
    
    @CreateEvent(compensationMethod="rejectOrder", approveMethod = "approveOrder")
    public void createOrder(Order order){
        ...
    }

    public void approveOrder(Order order){
        System.out.println("order start approve");
    }

    public void rejectOrder(Order order){
        System.out.println("order start reject");
    }
}
```
&emsp;&emsp;配置文件application.properties添加
```
#kafka config
publish.approve.topic = approveOrder
publish.reject.topic = rejectOrder

subscribe.approve.topic = approveCustomer
subscribe.reject.topic = rejectCustomer

#druid config
spring.datasource.url = jdbc:mysql://localhost:3306/order?useSSL=false
spring.datasource.username = root
spring.datasource.password = 123456
spring.datasource.driverClassName = com.mysql.jdbc.Driver
spring.datasource.type = com.alibaba.druid.pool.DruidDataSource
spring.datasource.poolPingConnectionsNotUsedFor= 60000
spring.datasource.removeAbandoned= true
spring.datasource.connectionProperties= druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
spring.datasource.minIdle= 1
spring.datasource.validationQuery= SELECT 1 FROM DUAL
spring.datasource.initialSize= 5
spring.datasource.maxWait= 60000
spring.datasource.poolPreparedStatements= false
spring.datasource.filters= stat,wall
spring.datasource.testOnBorrow=false
spring.datasource.testWhileIdle= true
spring.datasource.minEvictableIdleTimeMillis= 300000
spring.datasource.timeBetweenEvictionRunsMillis= 60000
spring.datasource.testOnReturn= false
spring.datasource.maxActive= 50
```
注：完整的程序样例见star文件夹
### 未来扩展
* 仅支持具有明确的分支事务执行序列的业务应用，对于不明确的业务应用则需要技术人员对其进行修改，以确定其执行序列，未来将支持由框架自行确定其执行序列，而不用手动修改；
* 事件驱动架构中需要保证消息队列事件发送和本地数据库事务的原子性问题，于是事件状态表需要与业务数据库共享数据源，占用了业务数据库的资源，未来将考虑实现对多数据源的事务管理；
* 全局写排他锁的实现需要代理数据库的JDBC数据源以及对SQL语句进行解析，STAR框架目前支持的数据库和SQL语句有限，未来将支持更多的数据库类型和SQL语句类型；
* STAR框架虽然存在全局日志，技术人员可以通过分析全局日志进行链路追踪和错误定位，但是并不直观，需要技术人员手动查询日志，未来将考虑全局日志的可视化，使得技术人员可以通过网站页面直观地进行分析与定位。

### 其他
&emsp;&emsp;如有问题，可以发送邮件至1458007138@qq.com，谢谢。

