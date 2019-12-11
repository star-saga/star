package org.event.driven.light.omegacommon.processor;

import org.event.driven.light.kafkaserialize.common.LocalMessage;
import org.event.driven.light.kafkaserialize.dbconnection.MessageService;
import org.event.driven.light.kafkaserialize.serialize.JsonDeserialize;
import org.event.driven.light.kafkaserialize.serialize.KryoMessageFormat;
import org.event.driven.light.omegacommon.common.CallbackContext;
import org.event.driven.light.omegacommon.common.TimestampUtils;

public class ApproveProcessor implements EventConsumeProcessor {
    @Override
    public void eventProcess(String globalId, MessageService messageService, CallbackContext callbackContext){
        LocalMessage localMessage = messageService.getReflectMethod(globalId);
        //System.out.println("localMessage: "+localMessage);
        if(localMessage!=null) {
            String expireTime = TimestampUtils.timestamp2String(localMessage.getExpireTime());
            //JsonDeserialize jsonDeserialize=new JsonDeserialize();
            //Object payloads = jsonDeserialize.deserialize("", localMessage.getPayloads());
            Object[] payloads = KryoMessageFormat.deserialize(localMessage.getPayloads());
            callbackContext.apply(globalId, localMessage.getLocalId(), expireTime, localMessage.getApproveMethod(), payloads);
        }
    }
}
