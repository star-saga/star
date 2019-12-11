package org.event.driven.light.datasource.proxy;

import org.event.driven.light.datasource.common.ConnectionContext;
import org.event.driven.light.datasource.common.LockRetryController;
import org.event.driven.light.datasource.exception.LockConflictException;
import org.event.driven.light.datasource.exception.TransactionException;
import org.event.driven.light.datasource.exception.TransactionExceptionCode;
import org.event.driven.light.datasource.lock.DefaultLockManager;
import org.event.driven.light.datasource.utils.StringUtils;
import org.event.driven.light.kafkaserialize.common.LightContext;
import org.event.driven.light.kafkaserialize.core.RootContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class ConnectionProxy extends AbstractConnectionProxy {

    private ConnectionContext context = new ConnectionContext();

    private final static LockRetryPolicy LOCK_RETRY_POLICY = new LockRetryPolicy();

    private DefaultLockManager defaultLockManager = new DefaultLockManager();

    private LightContext lightContext;

    public ConnectionProxy(DataSourceProxy dataSourceProxy, Connection targetConnection, LightContext lightContext) {
        super(dataSourceProxy, targetConnection);
        this.lightContext = lightContext;
    }

    public ConnectionContext getContext() {
        return context;
    }

    public void appendLockKey(String lockKey) {
        context.appendLockKey(lockKey);
    }

    public void bind(String globalId) {
        context.bind(globalId);
    }

    public void setGlobalLockRequire(boolean isLock) {
        context.setGlobalLockRequire(isLock);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if ((autoCommit) && !getAutoCommit()) {
            targetConnection.commit();
        }
        targetConnection.setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {
        try {
            LOCK_RETRY_POLICY.execute(() -> {
                doCommit();
                return null;
            });
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    private void doCommit() throws SQLException {
        if (context.isGlobalLockRequire()) {
            if(RootContext.inFirstPhrase()) {
                processFirstCommitWithGlobalLocks();
            }else{
                processSecondCommitWithGlobalLocks();
            }
        } else {
            targetConnection.commit();
        }
    }

    private void processFirstCommitWithGlobalLocks() throws SQLException {
        //System.out.println("first phrase: before register lock!!!!!!!");
        String lockKeys = context.buildLockKeys();
        String globalId = context.getGlobalId();
        String resourceId = dataSourceProxy.getResourceId();

        register(lockKeys, globalId, resourceId);

        try {
            targetConnection.commit();
        } catch (Throwable ex) {
            throw new SQLException(ex);
        }

        //context.reset();
    }

    private void processSecondCommitWithGlobalLocks() throws SQLException {
        //System.out.println("second phrase: before check lock!!!!!!!");
        String lockKeys = context.buildLockKeys();

        context.bind(lightContext.getGlobalId());
        checkLock(lockKeys);

        try {
            targetConnection.commit();
        } catch (Throwable ex) {
            throw new SQLException(ex);
        }

        context.reset();
    }

    public void checkLock(String lockKeys) throws SQLException {
        // Just check lock without requiring lock by now.
        String xid = context.getGlobalId();
        String resourceId = dataSourceProxy.getResourceId();

        try {
            boolean lockable = defaultLockManager.isLockable(xid, resourceId, lockKeys);
            if (!lockable) {
                throw new LockConflictException();
            }
        } catch (TransactionException e) {
            recognizeLockKeyConflictException(e, lockKeys);
        }
    }

    public void register(String lockKeys, String xid, String resourceId) throws SQLException {

        try{
            boolean registerSuccess = defaultLockManager.acquireLock(xid, resourceId,lockKeys);
            if(!registerSuccess) {
                throw new LockConflictException();
            }
        }catch(TransactionException e){
            recognizeLockKeyConflictException(e, lockKeys);
        }
    }

    private void recognizeLockKeyConflictException(TransactionException te, String lockKeys) throws SQLException {
        if (te.getCode() == TransactionExceptionCode.LockKeyConflict) {
            StringBuilder reasonBuilder = new StringBuilder("get global lock fail, globalId:" + context.getGlobalId());
            if (StringUtils.isNotBlank(lockKeys)) {
                reasonBuilder.append(", lockKeys:" + lockKeys);
            }
            throw new LockConflictException(reasonBuilder.toString());
        } else {
            throw new SQLException(te);
        }
    }

    @Override
    public void rollback() throws SQLException {
        targetConnection.rollback();
        //System.out.println("==============start rollback!================");
    }

    public static class LockRetryPolicy {
        public <T> T execute(Callable<T> callable) throws Exception {
            return doRetryOnLockConflict(callable);
        }

        protected <T> T doRetryOnLockConflict(Callable<T> callable) throws Exception {
            LockRetryController lockRetryController = new LockRetryController();
            while (true) {
                try {
                    return callable.call();
                } catch (LockConflictException lockConflict) {
                    onException(lockConflict);
                    lockRetryController.sleep(lockConflict);
                } catch (Exception e) {
                    onException(e);
                    throw e;
                }
            }
        }

        /**
         * Callback on exception in doLockRetryOnConflict.
         *
         * @param e invocation exception
         * @throws Exception error
         */
        protected void onException(Exception e) throws Exception {
        }
    }
}
