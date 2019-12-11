package org.event.driven.light.datasource.lock;

import org.event.driven.light.datasource.common.RowLock;
import org.event.driven.light.datasource.exception.LockConflictException;
import org.event.driven.light.kafkaserialize.redisconnect.RedisService;
import org.event.driven.light.datasource.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RedisLocker {
    protected static final Logger LOGGER = LoggerFactory.getLogger(RedisLocker.class);


    public boolean isLockable(List<RowLock> rowLocks) {
        if (CollectionUtils.isEmpty(rowLocks)) {
            //no lock
            return true;
        }
        String resourceId = rowLocks.get(0).getResourceId();

        for (RowLock rowLock : rowLocks) {
            String xid = rowLock.getXid();
            String tableName = rowLock.getTableName();
            String pk = rowLock.getPk();

            String key = resourceId+"-"+tableName+"-"+pk;
            String value = RedisService.getValue(key);

           //System.out.println("==============xid: "+xid+"==================");
           // System.out.println("==============redis key: "+key+"==================");
           // System.out.println("==============redis value: "+value+"==================");
            if (value == null || value.equals("") || value.equals(xid)) {
                // Locked by me
                continue;
            } else {
                LOGGER.info("Global lock on [" + resourceId + "-" + tableName + "-" + pk + "] is holding by " + value);
                return false;
            }
        }
        return true;
    }

    public boolean acquireLock(List<RowLock> rowLocks) throws LockConflictException {
        if (CollectionUtils.isEmpty(rowLocks)) {
            //no lock
            return true;
        }
        String resourceId = rowLocks.get(0).getResourceId();

        for (RowLock rowLock : rowLocks) {
            String xid = rowLock.getXid();
            String tableName = rowLock.getTableName();
            String pk = rowLock.getPk();
            String key = resourceId+"-"+tableName+"-"+pk;

            synchronized (this) {
                String value = RedisService.getValue(key);
               // System.out.print("redis value: ");
               // System.out.print(value+"\n");
                if(value == null || value.equals("")) {
                  //  System.out.println("insert key:"+key+", xid: "+xid+" to Redis");
                    if (!RedisService.insert2Redis(key, xid)) {
                        LOGGER.info("Cannot insert lockKey: {" + key + ":" + xid + "} to Redis!");
                        return false;
                    }
                  //  System.out.println("insert xid:"+xid+", key: "+key+" to Redis list");
                    if(!RedisService.insertListValue(xid, key)){
                        LOGGER.info("Cannot insert {" + key + "} to " + xid + " list in Redis!");
                        return false;
                    }
                }else if(value.equals(xid)){
                    continue;
                }else{
                   // System.out.println("Lock Confliction, because lockKey:{"+key+"} is locked by transactionId:{"+value+"}");
                    throw new LockConflictException();
                }
            }
        }
        return true;
    }

    public boolean releaseBranchLock(List<RowLock> rowLocks) throws LockConflictException {
        if (CollectionUtils.isEmpty(rowLocks)) {
            //no lock
            return true;
        }
        String resourceId = rowLocks.get(0).getResourceId();

        for (RowLock rowLock : rowLocks) {
            String xid = rowLock.getXid();
            String tableName = rowLock.getTableName();
            String pk = rowLock.getPk();
            String key = resourceId+"-"+tableName+"-"+pk;

            synchronized (this) {
                String value = RedisService.getValue(key);

                if(value == null) {
                    continue;
                }else if(value.equals(xid)){
                    boolean status = RedisService.delValue(key);
                    if(!status){
                        LOGGER.info("Cannot delete lockKey: {" + key + ":" + xid + "} to Redis!");
                        return false;
                    }
                }else{
                    throw new LockConflictException();
                }
            }
        }
        return true;
    }
}
