package org.event.driven.light.omegacommon.publish;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

//import org.springframework.stereotype.Component;

public class CreateEventProducer {

    //@Autowired
    public final KafkaProducer<String, String> producer;

    public CreateEventProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //props.put("value.serializer", "org.event.driven.light.kafkaserialize.serialize.JsonSerialize");
        producer = new KafkaProducer<String, String>(props);
    }

    public void createEventProduce(String id, String topic) {
        //System.out.println("----------------produce topic: "+topic+", id: "+id+"-----------------");
        producer.send(new ProducerRecord<String, String>(topic, id));
        //producer.close();
    }

    public void stopEventProduce(){
        producer.close();
    }

    public static void main(String[] args){
        try {
            CreateEventProducer producer = new CreateEventProducer();
            while(true) {
                producer.createEventProduce("test kafka produce!", "shxtest1");
                Thread.sleep(5*1000);
            }
        }catch(Throwable throwable){
            System.out.println("producer got exception!");
        }
    }
}
