package org.event.driven.light.datasource.lock;

import org.event.driven.light.datasource.common.RowLock;
import org.event.driven.light.datasource.exception.TransactionException;
import org.event.driven.light.datasource.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultLockManager implements LockManager {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultLockManager.class);

    private RedisLocker redisLocker = new RedisLocker();

    @Override
    public boolean isLockable(String xid, String resourceId, String lockKey) throws TransactionException {
        List<RowLock> locks = collectRowLocks(lockKey, resourceId, xid);
        try {
            return redisLocker.isLockable(locks);
        } catch (Exception t) {
            LOGGER.error("isLockable error, xid:" + xid + ", resourceId:" + resourceId + ", lockKey:" + lockKey, t);
            return false;
        }
    }

    @Override
    public boolean acquireLock(String xid, String resourceId, String lockKey) throws TransactionException{
        List<RowLock> locks = collectRowLocks(lockKey, resourceId, xid);

        try{
            return redisLocker.acquireLock(locks);
        }catch (Exception e){
            LOGGER.error("acquire error, xid:" + xid + ", resourceId:" + resourceId + ", lockKey:" + lockKey, e);
            return false;
        }
    }

    @Override
    public boolean releaseBranchLock(String xid, String resourceId, String lockKey) throws TransactionException{
        List<RowLock> locks = collectRowLocks(lockKey, resourceId, xid);

        try{
            return redisLocker.releaseBranchLock(locks);
        }catch(Exception e){
            LOGGER.error("release lock error, xid:" + xid + ", resourceId:" + resourceId + ", lockKey:" + lockKey, e);
            return false;
        }
    }

    public List<RowLock> collectRowLocks(String lockKey, String resourceId, String xid){
        List<RowLock> locks=new ArrayList<>();

        String[] tableGroupedLockKeys = lockKey.split(";");
        for (String tableGroupedLockKey : tableGroupedLockKeys) {
            int idx = tableGroupedLockKey.indexOf(":");
            if (idx < 0) {
                return locks;
            }
            String tableName = tableGroupedLockKey.substring(0, idx);
            String mergedPKs = tableGroupedLockKey.substring(idx + 1);
            if (StringUtils.isBlank(mergedPKs)) {
                return locks;
            }
            String[] pks = mergedPKs.split(",");
            if (pks == null || pks.length == 0) {
                return locks;
            }
            for (String pk : pks) {
                if (StringUtils.isNotBlank(pk)) {
                    RowLock rowLock = new RowLock();
                    rowLock.setXid(xid);
                    rowLock.setTableName(tableName);
                    rowLock.setPk(pk);
                    rowLock.setResourceId(resourceId);
                    locks.add(rowLock);
                }
            }
        }
        return locks;
    }
}
