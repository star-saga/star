package org.event.driven.star.common.domain;

import java.io.Serializable;

public interface IdGenerator<T extends Serializable> {
    T nextId();
}
