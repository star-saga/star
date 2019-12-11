package org.event.driven.light.datasource.proxy;

import org.event.driven.light.kafkaserialize.common.LightContext;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public abstract class AbstractDataSourceProxy implements DataSource {
    /**
     * The Target data source.
     */
    protected DataSource targetDataSource;

    /**
     * Instantiates a new Abstract data source proxy.
     *
     * @param targetDataSource the target data source
     */
    public AbstractDataSourceProxy(DataSource targetDataSource) {
        this.targetDataSource = targetDataSource;
    }

    /**
     * Gets target data source.
     *
     * @return the target data source
     */
    public DataSource getTargetDataSource() {
        return targetDataSource;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return targetDataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return targetDataSource.isWrapperFor(iface);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return targetDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        targetDataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        targetDataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return targetDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return targetDataSource.getParentLogger();
    }
}
