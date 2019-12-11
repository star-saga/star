package org.event.driven.light.datasource.proxy;

import org.event.driven.light.kafkaserialize.common.LightContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceProxy extends AbstractDataSourceProxy {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceProxy.class);
    private String resourceGroupId;
    private static final String DEFAULT_RESOURCE_GROUP_ID = "DEFAULT";
    private String jdbcUrl;
    private LightContext lightContext;

    public DataSourceProxy(DataSource targetDataSource, LightContext lightContext){
        this(targetDataSource, DEFAULT_RESOURCE_GROUP_ID, lightContext);
    }

    public DataSourceProxy(DataSource targetDataSource, String resourceGroupId, LightContext lightContext) {
        super(targetDataSource);
        this.init(targetDataSource, resourceGroupId, lightContext);
    }

    private void init(DataSource dataSource, String resourceGroupId, LightContext lightContext) {
        this.resourceGroupId = resourceGroupId;
        this.lightContext = lightContext;
        try (Connection connection = dataSource.getConnection()) {
            jdbcUrl = connection.getMetaData().getURL();
        } catch (SQLException e) {
            throw new IllegalStateException("can not init dataSource", e);
        }
    }

    @Override
    public ConnectionProxy getConnection() throws SQLException {
        Connection targetConnection = targetDataSource.getConnection();
        return new ConnectionProxy(this, targetConnection, lightContext);
    }

    @Override
    public ConnectionProxy getConnection(String username, String password) throws SQLException {
        Connection targetConnection = targetDataSource.getConnection(username, password);
        return new ConnectionProxy(this, targetConnection, lightContext);
    }

    public String getResourceId() {
        if (jdbcUrl.contains("?")) {
            return jdbcUrl.substring(0, jdbcUrl.indexOf("?"));
        } else {
            return jdbcUrl;
        }
    }
}
