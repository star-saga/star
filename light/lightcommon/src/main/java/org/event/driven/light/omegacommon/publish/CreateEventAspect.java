package org.event.driven.light.omegacommon.publish;

import jdk.nashorn.internal.codegen.CompilerConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.weaver.ast.Call;
import org.event.driven.light.kafkaserialize.common.LocalMessage;
import org.event.driven.light.kafkaserialize.core.RootContext;
import org.event.driven.light.kafkaserialize.dbconnection.MessageService;
import org.event.driven.light.kafkaserialize.serialize.JsonSerialize;
import org.event.driven.light.kafkaserialize.serialize.KryoMessageFormat;
import org.event.driven.light.omegacommon.annotations.CreateEvent;
import org.event.driven.light.kafkaserialize.common.LightContext;
import org.event.driven.light.omegacommon.common.CallbackContext;
import org.event.driven.light.omegacommon.common.TimestampUtils;
import org.event.driven.light.omegacommon.config.PublishTopicConfig;
import org.event.driven.light.omegacommon.config.ServiceConfig;
import org.event.driven.light.omegacommon.domain.SagaApprovedEvent;
import org.event.driven.light.omegacommon.domain.SagaRejectedEvent;
import org.event.driven.light.omegacommon.processor.ApproveProcessor;
import org.event.driven.light.omegacommon.processor.LockProcessor;
import org.event.driven.light.omegacommon.processor.RejectProcessor;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


@Aspect
public class CreateEventAspect {

    private CreateEventProducer createEventProducer;
    private ServiceConfig serviceConfig;
    private PublishTopicConfig publishTopicConfig;
    private LightContext lightContext;
    private MessageService messageService;
    private CallbackContext callbackContext;

    public CreateEventAspect(ServiceConfig serviceConfig, PublishTopicConfig publishTopicConfig,
                             LightContext lightContext, MessageService messageService,
                             CallbackContext callbackContext, CreateEventProducer createEventProducer){
        this.createEventProducer = createEventProducer;
        this.serviceConfig = serviceConfig;
        this.publishTopicConfig = publishTopicConfig;
        this.lightContext = lightContext;
        this.messageService = messageService;
        this.callbackContext = callbackContext;
    }

    @Around("execution(@org.event.driven.light.omegacommon.annotations.CreateEvent * *(..)) && @annotation(createEvent)")
    public Object Around(ProceedingJoinPoint joinPoint, CreateEvent createEvent) throws Throwable{
        //System.out.println("the name of service is: "+serviceConfig.serviceName());
        String globalId=lightContext.getGlobalId();
        String parentId=lightContext.getLocalId();
        String expireTime=lightContext.getExpireTime();
        String localId=lightContext.newLocalId();
        lightContext.setLocalId(localId);

        Timestamp current=new Timestamp(System.currentTimeMillis());
        Timestamp expire=TimestampUtils.string2Timestemp(expireTime);

        Method method=((MethodSignature)joinPoint.getSignature()).getMethod();
        String compensationMethod=formatCompenMethod(joinPoint, createEvent, method);
        String approveMethod=formatApproveMethod(joinPoint, createEvent, method);
        //JsonSerialize jsonSerialize=new JsonSerialize();
        //byte[] payloads = jsonSerialize.serialize("", joinPoint.getArgs());
        byte[] payloads= KryoMessageFormat.serialize(joinPoint.getArgs());
        LocalMessage lm=new LocalMessage(globalId, parentId, localId, current, expire, 0,
                0, compensationMethod, approveMethod, payloads);

        RootContext.bind(globalId);
        RootContext.bindGlobalLockFlag();
        RootContext.bindFirstPhraseFlag();

        //System.out.println("around globalId: "+globalId+" , parentId: "+parentId+
        //        "  , localId: "+localId+"  , expireTime: "+expireTime);

        messageService.saveLocalMessage(lm);

        //System.out.println(joinPoint.getArgs().toString());
        Object result = null;
        try {
            result = joinPoint.proceed();
        }catch(Exception e){
           // System.out.println("Error when execute joinpoint proceed");
            e.printStackTrace();
            RootContext.unbindFirstPhraseFlag();
            RejectProcessor rejectProcessor=new RejectProcessor();
            rejectProcessor.eventProcess(globalId, messageService,callbackContext);

            //SagaRejectedEvent rejectedEvent = new SagaRejectedEvent(globalId);
            createEventProducer.createEventProduce(globalId, publishTopicConfig.publishRejectTopic());
            return null;
        }

        if (RootContext.isLastService()) {
            //System.out.println("I'm the last service!");
            RootContext.unbindFirstPhraseFlag();
            LockProcessor lockProcessor = new LockProcessor();
            lockProcessor.releaseGlobalLock(globalId);
            ApproveProcessor approveProcessor=new ApproveProcessor();
            approveProcessor.eventProcess(globalId, messageService, callbackContext);

            //SagaApprovedEvent approvedEvent=new SagaApprovedEvent(globalId);
            createEventProducer.createEventProduce(globalId, publishTopicConfig.publishApproveTopic());
        }

        return result;
    }

    @Around(value= "execution(@org.event.driven.light.omegacommon.annotations.StartEvent * *(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        //System.out.println("------------startEvent intercept!--------------------");
        lightContext.setGlobalId(lightContext.newGlobalId());

        Timestamp expiryTime = new Timestamp(System.currentTimeMillis()+5*60*1000);
        lightContext.setExpireTime(TimestampUtils.timestamp2String(expiryTime));

        RootContext.bindBackendServiceFlag();
        Object result=joinPoint.proceed();
        return result;
    }

    public String formatCompenMethod(ProceedingJoinPoint joinPoint, CreateEvent createEvent, Method method)
            throws NoSuchMethodException{
        return joinPoint.getTarget().getClass()
                .getDeclaredMethod(createEvent.compensationMethod(), method.getParameterTypes())
                .toString();
    }

    public String formatApproveMethod(ProceedingJoinPoint joinPoint, CreateEvent createEvent, Method method)
            throws NoSuchMethodException{
        return joinPoint.getTarget().getClass()
                .getDeclaredMethod(createEvent.approveMethod(), method.getParameterTypes())
                .toString();
    }

}
