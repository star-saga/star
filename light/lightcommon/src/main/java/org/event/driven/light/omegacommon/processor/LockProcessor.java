package org.event.driven.light.omegacommon.processor;

import org.event.driven.light.kafkaserialize.redisconnect.RedisService;

import java.util.List;

public class LockProcessor {

    public boolean releaseGlobalLock(String globalId) throws Exception {
        //System.out.println("start release global lock! globalId:"+globalId);

        List<String> lockKeys= RedisService.getListValue(globalId);
        //System.out.println("release globalId: "+globalId+",  lockKey: "+lockKeys.toString());
        for(String lockKey: lockKeys){
            synchronized (this) {
                String value = RedisService.getValue(lockKey);
                //System.out.println("release lockKey: "+lockKey+", value: "+value);
                if(value == null) {
                    continue;
                }else if(value.equals(globalId)){
                    boolean status = RedisService.delValue(lockKey);
                    if(!status){
                        //System.out.println("Cannot delete lockKey: {" + lockKey + ":" + globalId + "} to Redis!");
                        return false;
                    }
                }else{
                    throw new Exception();
                }
            }
        }

        return true;
    }

}
