package org.event.driven.light.omegacommon.domain;

import java.io.Serializable;

public class SagaApprovedEvent implements DomainEvent, Serializable {
    private String globalId;

    public SagaApprovedEvent(String globalId){
        this.globalId = globalId;
    }

    public String globalId(){
        return globalId;
    }
}
