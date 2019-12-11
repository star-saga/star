package org.event.driven.star.common.domain;

import java.util.UUID;

public class UniqueIdGenerator implements IdGenerator<String> {
    @Override
    public String nextId() {
        return UUID.randomUUID().toString();
    }
}
