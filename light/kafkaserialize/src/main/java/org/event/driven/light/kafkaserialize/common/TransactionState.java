package org.event.driven.light.kafkaserialize.common;

public enum TransactionState {
    START, END, COMPENSATED, TIMEOUT, EXCEPTION
}
