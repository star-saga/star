package org.event.driven.light.omegacommon.subscribe;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.event.driven.light.kafkaserialize.common.TransferMessage;
import org.event.driven.light.kafkaserialize.core.RootContext;
import org.event.driven.light.kafkaserialize.dbconnection.MessageService;
import org.event.driven.light.omegacommon.common.CallbackContext;
import org.event.driven.light.omegacommon.config.PublishTopicConfig;
import org.event.driven.light.omegacommon.domain.SagaApprovedEvent;
import org.event.driven.light.omegacommon.domain.SagaRejectedEvent;
import org.event.driven.light.omegacommon.processor.ApproveProcessor;
import org.event.driven.light.omegacommon.processor.EventConsumeProcessor;
import org.event.driven.light.omegacommon.processor.LockProcessor;
import org.event.driven.light.omegacommon.processor.RejectProcessor;
import org.event.driven.light.omegacommon.publish.CreateEventProducer;
import org.springframework.stereotype.Component;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Properties;

@Component
public class CreateEventConsumer {
    public KafkaConsumer<String, String> kafkaConsumer=null;
    //public final static String TOPIC = "shxtest1";

    public CreateEventConsumer(){}

    public CreateEventConsumer(String topic){
        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
        props.put("group.id", topic);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "earliest");
        props.put("session.timeout.ms", "30000");
        props.put("max.poll.interval.ms", "200000");
        props.put("max.poll.records", "10");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //props.put("value.deserializer", "org.event.driven.light.kafkaserialize.serialize.JsonDeserialize");
        kafkaConsumer = new KafkaConsumer<String, String>(props);
    }

    public void approvedEventConsume(String subscriberId, String topic, MessageService messageService, CallbackContext callbackContext,
                                     CreateEventProducer createEventProducer, PublishTopicConfig publishTopicConfig) {
        //System.out.println("----------------consume topic: "+topic+"-------------------");
        if(topic.equals("none")){
            return;
        }

        try{
            kafkaConsumer.subscribe(Arrays.asList(topic));
            new Thread(()->{
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                    for (ConsumerRecord<String, String> record : records) {
                        //System.out.println("-----------------");
                        //System.out.printf("approve offset = %d, value = %s", record.offset(), record.value());
                        //System.out.println();
                        //SagaApprovedEvent sagaApprovedEvent = (SagaApprovedEvent) record.value();
                        String globalId = record.value();
                        EventConsumeProcessor eventConsumeProcessor = new ApproveProcessor();
                        eventConsumeProcessor.eventProcess(globalId, messageService, callbackContext);

                        createEventProducer.createEventProduce(globalId, publishTopicConfig.publishApproveTopic());
                    }
                }
            }, subscriberId).start();
        }catch (Exception e) {
            throw e;
        }
    }

    public void rejectedEventConsume(String subscriberId, String topic, MessageService messageService, CallbackContext callbackContext,
                                     CreateEventProducer createEventProducer, PublishTopicConfig publishTopicConfig, String serviceName){
        //System.out.println("----------------consume topic: "+topic+"-------------------");
        if(topic.equals("none")){
            return;
        }

        try{
            kafkaConsumer.subscribe(Arrays.asList(topic));
            new Thread(()-> {
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                    for (ConsumerRecord<String, String> record : records) {
                       // System.out.println("-----------------");
                       // System.out.printf("reject offset = %d, value = %s", record.offset(), record.value());
                       // System.out.println();
                        //SagaRejectedEvent sagaRejectedEvent = (SagaRejectedEvent) record.value();
                        String globalId = record.value();

                        if (serviceName.equals("backend")) {
                         //   System.out.println("I'm the backend service");
                            LockProcessor lockProcessor = new LockProcessor();
                            try {
                                lockProcessor.releaseGlobalLock(globalId);
                            } catch (Exception e) {
                                System.out.println("The Backend Service release global Lock Failed!");
                            }

                        }

                        EventConsumeProcessor eventConsumeProcessor = new RejectProcessor();
                        eventConsumeProcessor.eventProcess(globalId, messageService, callbackContext);

                        createEventProducer.createEventProduce(globalId, publishTopicConfig.publishRejectTopic());
                    }
                }
            }, subscriberId).start();
        }catch (Exception e) {
            throw e;
        }


    }

    public void stopEventConsume(){
        kafkaConsumer.close();
    }

    public static void main(String[] args){
        try {
            //CreateEventConsumer consumer = new CreateEventConsumer();
            //consumer.createEventConsume("shxtest1");
        }catch(Throwable throwable){
            System.out.println("got exception!");
        }
    }
}
