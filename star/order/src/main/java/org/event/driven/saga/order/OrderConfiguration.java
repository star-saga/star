package org.event.driven.saga.order;

import org.event.driven.saga.order.service.OrderService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
public class OrderConfiguration {
    @Bean
    public OrderService orderService() {
        return new OrderService();
    }

}
