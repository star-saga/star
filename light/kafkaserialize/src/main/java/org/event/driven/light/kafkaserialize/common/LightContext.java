package org.event.driven.light.kafkaserialize.common;

import org.event.driven.light.kafkaserialize.common.IdGenerator;

public class LightContext {
    public static final String GLOBAL_ID="X-Pack-Global-Transaction-Id";
    public static final String LOCAL_ID="X-Pack-LOCAL_Transaction-Id";
    public static final String EXPIRE_TIME="X-Pack-Expire-Time";

    private final ThreadLocal<String> globalId=new InheritableThreadLocal<>();
    private final ThreadLocal<String> localId=new InheritableThreadLocal<>();
    private final ThreadLocal<String> expireTime=new InheritableThreadLocal<>();
    private final IdGenerator<String> idGenerator;

    public LightContext(IdGenerator<String> idGenerator){
        this.idGenerator = idGenerator;
    }

    public String newGlobalId() {
        String id=idGenerator.nextId();
        globalId.set(id);
        return id;
    }

    public void setGlobalId(String txId){
        globalId.set(txId);
    }

    public String getGlobalId(){
        return globalId.get();
    }

    public String newLocalId(){
        String id=idGenerator.nextId();
        localId.set(id);
        return id;
    }

    public void setLocalId(String txId){
        localId.set(txId);
    }

    public String getLocalId(){
        return localId.get();
    }

    public void setExpireTime(String exTime){
        expireTime.set(exTime);
    }

    public String getExpireTime(){
        return expireTime.get();
    }

    public void clear(){
        globalId.remove();
        localId.remove();
        expireTime.remove();
    }

    @Override
    public String toString(){
        return "LightContext{ "+
                "globalTxId= "+globalId.get() +
                ", localTxId= "+localId.get() +
                ", expireTimes= "+expireTime.get() +
                " }";
    }
}
