package org.event.driven.light.kafkaserialize.serialize;

import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class JsonDeserialize implements Deserializer<Object> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
//        Object[] obj=KryoMessageFormat.deserialize(data);
//        return obj[0];
        return BeanUtils.byte2Obj(data);
    }

    @Override
    public void close() {
    }
}
