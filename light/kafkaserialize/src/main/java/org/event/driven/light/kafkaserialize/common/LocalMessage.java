package org.event.driven.light.kafkaserialize.common;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name="LocalMessage")
@Access(AccessType.FIELD)
public class LocalMessage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String globalId;
    private String parentId;
    private String localId;
    private Timestamp startTime;
    private Timestamp expireTime;
    private int messageState;
    private int transactionState;
    private String compensateMethod;
    private String approveMethod;
    private byte[] payloads;

    public LocalMessage(String globalId, String parentId, String localId,
                        Timestamp startTime, Timestamp expireTime,
                        int messageState, int transactionState,
                        String compensateMethod, String approveMethod, byte[] payloads){
        this.globalId = globalId;
        this.parentId = parentId;
        this.localId = localId;
        this.startTime = startTime;
        this.expireTime = expireTime;
        this.messageState = messageState;
        this.transactionState = transactionState;
        this.compensateMethod = compensateMethod;
        this.approveMethod = approveMethod;
        this.payloads = payloads;
    }

    public String getGlobalId(){
        return globalId;
    }

    public String getParentId(){
        return parentId;
    }

    public String getLocalId() {
        return localId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }

    public int getMessageState() {
        return messageState;
    }

    public int getTransactionState(){
        return transactionState;
    }

    public String getCompensateMethod(){
        return compensateMethod;
    }

    public String getApproveMethod(){
        return approveMethod;
    }

    public byte[] getPayloads() {
        return payloads;
    }

    public void setGlobalId(String globalId){
        this.globalId = globalId;
    }

    public void setLocalId(String localId){
        this.localId = localId;
    }

    public void setExpireTime(Timestamp expireTime){
        this.expireTime = expireTime;
    }

    public void setPayloads(byte[] payloads){
        this.payloads = payloads;
    }

    public void setApproveMethod(String approveMethod){
        this.approveMethod = approveMethod;
    }
}
