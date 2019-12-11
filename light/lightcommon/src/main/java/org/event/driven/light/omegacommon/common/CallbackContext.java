package org.event.driven.light.omegacommon.common;

import org.event.driven.light.kafkaserialize.common.LightContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallbackContext {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<String, CallbackContextInternal> contexts = new ConcurrentHashMap<>();
    private final LightContext lightContext;

    public CallbackContext(LightContext lightContext) {
        this.lightContext = lightContext;
    }

    public void addCallbackContext(String key, Method compensationMethod, Object target) {
        //System.out.println("------------------addCallbackContext--------------- ");
        //System.out.println("target: "+target.toString());
        //System.out.println("compensationMethod: "+compensationMethod.toString());
        compensationMethod.setAccessible(true);
        contexts.put(key, new CallbackContextInternal(target, compensationMethod));
    }

    public void apply(String globalTxId, String localTxId, String expireTime, String callbackMethod, Object... payloads) {
        //System.out.println("start execute callbackContext apply method.");

        CallbackContextInternal contextInternal = contexts.get(callbackMethod);
        String oldGlobalTxId = lightContext.getGlobalId();
        String oldLocalTxId = lightContext.getLocalId();
        String oldExpireTime = lightContext.getExpireTime();
        try {
            lightContext.setGlobalId(globalTxId);
            lightContext.setLocalId(localTxId);
            lightContext.setExpireTime(expireTime);

            contextInternal.callbackMethod.invoke(contextInternal.target, payloads);
            LOG.info("Callback transaction with global tx id [{}], local tx id [{}]", globalTxId, localTxId);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(
                    "Pre-checking for callback method " + contextInternal.callbackMethod.toString()
                            + " was somehow skipped, did you forget to configure callback method checking on service startup?",
                    e);
        } finally {
            lightContext.setGlobalId(oldGlobalTxId);
            lightContext.setLocalId(oldLocalTxId);
            lightContext.setExpireTime(oldExpireTime);
        }
    }


    private static final class CallbackContextInternal {
        private final Object target;

        private final Method callbackMethod;

        private CallbackContextInternal(Object target, Method callbackMethod) {
            this.target = target;
            this.callbackMethod = callbackMethod;
        }
    }
}
