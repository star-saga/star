package org.event.driven.light.kafkaserialize.serialize;

import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class JsonSerialize implements Serializer<Object> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Object data) {
//        Object[] obj={data};
//        return KryoMessageFormat.serialize(obj);
        return BeanUtils.bean2Byte(data);
    }

    @Override
    public void close() {
    }
}
