package org.event.driven.light.kafkaserialize.common;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="LocalLog")
@Access(AccessType.FIELD)
public class LocalLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String globalId;
    private String parentId;
    private String localId;
    private Timestamp startTime;
    private Timestamp expireTime;
    private String localService;
    private String publishService;
    private String subscribeService;
    private String eventType;
    private TransactionState transactionState;
    private int retries;
    private String exceptionMessage;

    public LocalLog(String globalId, String parentId, String localId,
                    Timestamp startTime, Timestamp expireTime,
                    String localService, String publishService, String subscribeService,
                    String eventType, TransactionState transactionState,
                    int retries, String exceptionMessage){
            this.globalId = globalId;
            this.parentId = parentId;
            this.localId = localId;
            this.startTime = startTime;
            this.expireTime = expireTime;
            this.localService = localService;
            this.publishService = publishService;
            this.subscribeService = subscribeService;
            this.eventType = eventType;
            this.transactionState = transactionState;
            this.retries = retries;
            this.exceptionMessage = exceptionMessage;
    }

    public String getGlobalId(){
        return globalId;
    }

    public String getParentId(){
        return parentId;
    }

    public String getLocalId(){
        return localId;
    }

    public Timestamp getStartTime(){
        return startTime;
    }

    public Timestamp getExpireTime(){
        return expireTime;
    }

    public String getLocalService(){
        return localService;
    }

    public String getPublishService(){
        return publishService;
    }


    public String getSubscribeService(){
        return subscribeService;
    }

    public String getEventType(){
        return eventType;
    }

    public TransactionState getTransactionState(){
        return transactionState;
    }

    public int getRetries(){
        return retries;
    }

    public String getExceptionMessage(){
        return exceptionMessage;
    }
}
