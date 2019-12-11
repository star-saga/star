package org.event.driven.light.omegacommon.common;

import org.event.driven.light.omegacommon.annotations.CreateEvent;

import java.lang.reflect.Method;

public class ApproveMethodCheckingCallback extends MethodCheckingCallback {
    public ApproveMethodCheckingCallback(Object bean, CallbackContext callbackContext) {
        super(bean, callbackContext);
    }

    @Override
    public void doWith(Method method) throws IllegalArgumentException {
        if (!method.isAnnotationPresent(CreateEvent.class)) {
            return;
        }
        CreateEvent approve = method.getAnnotation(CreateEvent.class);
        String approveMethod = approve.approveMethod();
        loadMethodContext(method, approveMethod);
    }
}
