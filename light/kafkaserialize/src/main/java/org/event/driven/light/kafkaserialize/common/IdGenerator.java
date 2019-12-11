package org.event.driven.light.kafkaserialize.common;

import java.io.Serializable;

public interface IdGenerator <T extends Serializable> {
    T nextId();
}
