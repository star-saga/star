package org.event.driven.light.omegacommon.domain;

import java.io.Serializable;

public class SagaRejectedEvent implements DomainEvent, Serializable {
    private String globalId;

    public SagaRejectedEvent(String globalId){
        this.globalId = globalId;
    }

    public String globalId(){
        return globalId;
    }
}
