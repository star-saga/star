package org.event.driven.light.omegacommon.processor;

import org.event.driven.light.kafkaserialize.common.LightContext;
import org.event.driven.light.omegacommon.common.ApproveMethodCheckingCallback;
import org.event.driven.light.omegacommon.common.CallbackContext;
import org.event.driven.light.omegacommon.common.CompensableMethodCheckingCallback;
import org.event.driven.light.omegacommon.common.ExecutorFieldCallback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

public class SecondPhraseProcessor implements BeanPostProcessor {
    private final LightContext lightContext;
    private final CallbackContext callbackContext;

    public SecondPhraseProcessor(LightContext lightContext, CallbackContext callbackContext){
        this.lightContext = lightContext;
        this.callbackContext = callbackContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        checkMethod(bean);
        checkFields(bean);
        return bean;
    }

    private void checkMethod(Object bean){
        ReflectionUtils.doWithMethods(
                bean.getClass(),
                new CompensableMethodCheckingCallback(bean, callbackContext));

        ReflectionUtils.doWithMethods(
                bean.getClass(),
                new ApproveMethodCheckingCallback(bean,callbackContext));
    }

    private void checkFields(Object bean) {
        ReflectionUtils.doWithFields(bean.getClass(), new ExecutorFieldCallback(bean, lightContext));
    }
}
