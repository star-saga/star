package org.event.driven.light.omegacommon.common;

import org.event.driven.light.kafkaserialize.common.LightContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

public class ExecutorFieldCallback implements ReflectionUtils.FieldCallback {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LightContext lightContext;
    private final Object bean;

    public ExecutorFieldCallback(Object bean, LightContext lightContext) {
        this.lightContext = lightContext;
        this.bean = bean;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        if (!field.isAnnotationPresent(OmegaContextAware.class)) {
            return;
        }

        ReflectionUtils.makeAccessible(field);

        Class<?> generic = field.getType();

        if (!Executor.class.isAssignableFrom(generic)) {
            throw new IllegalArgumentException(
                    "Only Executor, ExecutorService, and ScheduledExecutorService are supported for @"
                            + OmegaContextAware.class.getSimpleName());
        }

        field.set(bean, ExecutorProxy.newInstance(field.get(bean), field.getType(), lightContext));
    }

    private static class RunnableProxy implements InvocationHandler {

        private final String globalTxId;
        private final String localTxId;
        private final String expireTime;
        private final Object runnable;
        private final LightContext lightContext;

        private static Object newInstance(Object runnable, LightContext omegaContext) {
            RunnableProxy runnableProxy = new RunnableProxy(omegaContext, runnable);
            return Proxy.newProxyInstance(
                    runnable.getClass().getClassLoader(),
                    runnable.getClass().getInterfaces(),
                    runnableProxy);
        }

        private RunnableProxy(LightContext lightContext, Object runnable) {
            this.lightContext = lightContext;
            this.globalTxId = lightContext.getGlobalId();
            this.localTxId = lightContext.getLocalId();
            this.expireTime = lightContext.getExpireTime();
            this.runnable = runnable;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                LOG.debug("Setting OmegaContext with globalTxId [{}] & localTxId [{}]",
                        globalTxId,
                        localTxId);

                lightContext.setGlobalId(globalTxId);
                lightContext.setLocalId(localTxId);
                lightContext.setExpireTime(expireTime);

                return method.invoke(runnable, args);
            } finally {
                lightContext.clear();
                LOG.debug("Cleared OmegaContext with globalTxId [{}] & localTxId [{}]",
                        globalTxId,
                        localTxId);
            }
        }
    }

    private static class ExecutorProxy implements InvocationHandler {
        private final Object target;
        private final LightContext lightContext;

        private ExecutorProxy(Object target, LightContext lightContext) {
            this.target = target;
            this.lightContext = lightContext;
        }

        private static Object newInstance(Object target, Class<?> targetClass, LightContext omegaContext) {
            Class<?>[] interfaces = targetClass.isInterface() ? new Class<?>[] {targetClass} : targetClass.getInterfaces();

            return Proxy.newProxyInstance(
                    targetClass.getClassLoader(),
                    interfaces,
                    new ExecutorProxy(target, omegaContext));
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(target, augmentRunnablesWithOmegaContext(args));
        }

        private Object[] augmentRunnablesWithOmegaContext(Object[] args) {
            Object[] augmentedArgs = new Object[args.length];

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (isExecutable(arg)) {
                    augmentedArgs[i] = RunnableProxy.newInstance(arg, lightContext);
                } else if (isCollectionOfExecutables(arg)) {
                    List argList = new ArrayList();
                    Collection argCollection = (Collection<?>) arg;
                    for (Object a : argCollection) {
                        argList.add(RunnableProxy.newInstance(a, lightContext));
                    }
                    augmentedArgs[i] = argList;
                } else {
                    augmentedArgs[i] = arg;
                }
            }

            return augmentedArgs;
        }

        private boolean isExecutable(Object arg) {
            return arg instanceof Runnable || arg instanceof Callable;
        }

        private boolean isCollectionOfExecutables(Object arg) {
            return arg instanceof Collection
                    && !((Collection<?>) arg).isEmpty()
                    && isExecutable(((Collection<?>) arg).iterator().next());
        }
    }
}
