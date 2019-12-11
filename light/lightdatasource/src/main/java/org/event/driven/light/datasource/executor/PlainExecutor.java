package org.event.driven.light.datasource.executor;

import org.event.driven.light.datasource.common.StatementCallback;
import org.event.driven.light.datasource.proxy.StatementProxy;

import java.sql.Statement;

public class PlainExecutor <T, S extends Statement> implements Executor {
    private StatementProxy<S> statementProxy;

    private StatementCallback<T, S> statementCallback;

    /**
     * Instantiates a new Plain executor.
     *
     * @param statementProxy    the statement proxy
     * @param statementCallback the statement callback
     */
    public PlainExecutor(StatementProxy<S> statementProxy, StatementCallback<T, S> statementCallback) {
        this.statementProxy = statementProxy;
        this.statementCallback = statementCallback;
    }

    @Override
    public T execute(Object... args) throws Throwable {
        return statementCallback.execute(statementProxy.getTargetStatement(), args);
    }
}
