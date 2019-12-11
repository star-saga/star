package org.event.driven.light.omegacommon.subscribe;

import org.event.driven.light.kafkaserialize.common.TransferMessage;
import org.event.driven.light.kafkaserialize.dbconnection.MessageService;
import org.event.driven.light.omegacommon.common.CallbackContext;
import org.event.driven.light.omegacommon.common.CompensateRetryController;
import org.event.driven.light.omegacommon.config.PublishTopicConfig;
import org.event.driven.light.omegacommon.config.ServiceConfig;
import org.event.driven.light.omegacommon.config.SubscribeTopicConfig;
import org.event.driven.light.omegacommon.publish.CreateEventProducer;

import javax.annotation.PostConstruct;
import java.util.concurrent.Callable;

public class DomainEventDispatcher {

    private SubscribeTopicConfig subscribeTopicConfig;
    private MessageService messageService;
    private CallbackContext callbackContext;
    private CreateEventProducer createEventProducer;
    private PublishTopicConfig publishTopicConfig;
    private ServiceConfig serviceConfig;

    public DomainEventDispatcher(ServiceConfig serviceConfig, SubscribeTopicConfig subscribeTopicConfig, MessageService messageService,
                                 CallbackContext callbackContext, CreateEventProducer createEventProducer,
                                 PublishTopicConfig publishTopicConfig){
        this.serviceConfig = serviceConfig;
        this.subscribeTopicConfig = subscribeTopicConfig;
        this.messageService = messageService;
        this.callbackContext = callbackContext;
        this.createEventProducer = createEventProducer;
        this.publishTopicConfig = publishTopicConfig;
    }

    @PostConstruct
    public void initialize(){
        System.out.println("this is the postconstruct!");
        try {
            String subscriberId = "";

            subscriberId = "subscriberId_approve"+System.currentTimeMillis();
            CreateEventConsumer approveEventConsumer = new CreateEventConsumer(serviceConfig.serviceName()+"_approveGroup");
            approveEventConsumer.approvedEventConsume(subscriberId, subscribeTopicConfig.subscribeApproveTopic(), messageService, callbackContext, createEventProducer, publishTopicConfig);

            subscriberId = "subscriberId_reject"+System.currentTimeMillis();
            CreateEventConsumer rejectEventConsumer = new CreateEventConsumer(serviceConfig.serviceName()+"_rejectGroup");
            rejectEventConsumer.rejectedEventConsume(subscriberId, subscribeTopicConfig.subscribeRejectTopic(), messageService, callbackContext, createEventProducer, publishTopicConfig, serviceConfig.serviceName());

        }catch(Exception e){
            System.out.print("Got throwable Exception in createEventConsumer!");
        }
    }

}
