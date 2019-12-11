package org.event.driven.light.kafkaserialize.common;

import org.event.driven.light.kafkaserialize.common.IdGenerator;

import java.util.UUID;

public class UniqueIdGenerator implements IdGenerator<String> {
    @Override
    public String nextId() {
        return UUID.randomUUID().toString();
    }
}
