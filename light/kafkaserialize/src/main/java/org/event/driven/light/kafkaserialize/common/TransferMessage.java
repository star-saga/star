package org.event.driven.light.kafkaserialize.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class TransferMessage implements Serializable {
    private String globalId;
    private String localId;
    private Timestamp expireTime;
    //private byte[] payloads;

    public TransferMessage(String globalId, String localId, Timestamp expireTime/*,
                           byte[] payloads*/){
        this.globalId = globalId;
        this.localId = localId;
        this.expireTime = expireTime;
       // this.payloads = payloads;
    }

    public String getGlobalId(){
        return globalId;
    }

    public String getLocalId() {
        return localId;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }

//    public byte[] getPayloads() {
//        return payloads;
//    }

    public void setGlobalId(String globalId){
        this.globalId = globalId;
    }

    public void setLocalId(String localId){
        this.localId = localId;
    }

    public void setExpireTime(Timestamp expireTime){
        this.expireTime = expireTime;
    }

//    public void setPayloads(byte[] payloads){
//        this.payloads = payloads;
//    }

}
