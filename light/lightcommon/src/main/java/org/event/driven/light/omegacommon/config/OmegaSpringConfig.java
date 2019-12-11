package org.event.driven.light.omegacommon.config;

import java.util.*;

import org.event.driven.light.kafkaserialize.common.IdGenerator;
import org.event.driven.light.kafkaserialize.common.LightContext;
import org.event.driven.light.kafkaserialize.common.UniqueIdGenerator;
import org.event.driven.light.kafkaserialize.config.ContextConfig;
import org.event.driven.light.kafkaserialize.config.DBConfig;
import org.event.driven.light.kafkaserialize.dbconnection.MessageService;
import org.event.driven.light.omegacommon.common.CallbackContext;
import org.event.driven.light.omegacommon.processor.SecondPhraseProcessor;
import org.event.driven.light.omegacommon.publish.CreateEventAspect;
import org.event.driven.light.omegacommon.publish.CreateEventProducer;
import org.event.driven.light.omegacommon.subscribe.DomainEventDispatcher;
import org.event.driven.light.omegacommon.transport.TransactionClientHttpRequestInterceptor;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;

@Configuration
@Import({DBConfig.class, ContextConfig.class})
public class OmegaSpringConfig {

    @Bean
    IdGenerator<String> idGenerator(){
        return new UniqueIdGenerator();
    }

    @Bean
    CallbackContext callbackContext(LightContext lightContext){
        return new CallbackContext(lightContext);
    }

    @Bean
    public RestTemplate restTemplate(@Autowired(required = false) LightContext lightContext){
        RestTemplate template = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();
        interceptors.add(new TransactionClientHttpRequestInterceptor(lightContext));
        template.setInterceptors(interceptors);
        return template;
    }

    @Bean
    SecondPhraseProcessor secondPhraseProcessor(LightContext lightContext, CallbackContext callbackContext){
        return new SecondPhraseProcessor(lightContext, callbackContext);
    }

    @Bean
    ServiceConfig serviceConfig(@Value("${spring.application.name}")String serviceName) {
        return new ServiceConfig(serviceName);
    }

    @Bean
    PublishTopicConfig publishTopicConfig(@Value("${publish.create.topic}")String publishCreateTopic,
                                   @Value("${publish.approve.topic}")String publishApproveTopic,
                                   @Value("${publish.reject.topic}")String publishRejectTopic) {
        return new PublishTopicConfig(publishCreateTopic, publishApproveTopic, publishRejectTopic);
    }

    @Bean
    SubscribeTopicConfig subscribeTopicConfig(@Value("${subscribe.create.topic}")String subscribeCreateTopic,
                                              @Value("${subscribe.approve.topic}")String subscribeApproveTopic,
                                              @Value("${subscribe.reject.topic}")String subscribeRejectTopic){
        return new SubscribeTopicConfig(subscribeCreateTopic, subscribeApproveTopic, subscribeRejectTopic);
    }

    @Bean
    CreateEventProducer createEventProducer(){
        return new CreateEventProducer();
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcher(ServiceConfig serviceConfig, SubscribeTopicConfig subscribeTopicConfig, MessageService messageService,
                                                       CallbackContext callbackContext, CreateEventProducer createEventProducer,
                                                       PublishTopicConfig publishTopicConfig){
        return new DomainEventDispatcher(serviceConfig, subscribeTopicConfig, messageService, callbackContext, createEventProducer, publishTopicConfig);
    }

    @Bean
    CreateEventAspect createEventAspect(ServiceConfig serviceConfig, PublishTopicConfig publishTopicConfig,
                                        LightContext lightContext, MessageService messageService,
                                        CallbackContext callbackContext, CreateEventProducer createEventProducer){
        return new CreateEventAspect(serviceConfig, publishTopicConfig, lightContext, messageService, callbackContext, createEventProducer);
    }


}
