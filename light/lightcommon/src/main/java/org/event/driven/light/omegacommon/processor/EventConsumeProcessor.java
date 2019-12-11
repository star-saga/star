package org.event.driven.light.omegacommon.processor;

import org.event.driven.light.kafkaserialize.dbconnection.MessageService;
import org.event.driven.light.omegacommon.common.CallbackContext;

public interface EventConsumeProcessor {
    void eventProcess(String globalId, MessageService messageService, CallbackContext callbackContext);
}
