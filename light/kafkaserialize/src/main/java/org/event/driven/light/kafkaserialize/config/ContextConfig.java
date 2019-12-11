package org.event.driven.light.kafkaserialize.config;

import org.event.driven.light.kafkaserialize.common.IdGenerator;
import org.event.driven.light.kafkaserialize.common.LightContext;
import org.springframework.context.annotation.Bean;

public class ContextConfig {

    @Bean
    LightContext lightContext(IdGenerator<String> idGenerator){
        return new LightContext(idGenerator);
    }
}
