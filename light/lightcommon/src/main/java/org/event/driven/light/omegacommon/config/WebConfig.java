package org.event.driven.light.omegacommon.config;

import org.event.driven.light.kafkaserialize.common.LightContext;
import org.event.driven.light.omegacommon.transport.TransactionHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    private final LightContext lightContext;

    @Autowired
    public WebConfig(@Autowired(required = false) LightContext lightContext){
        this.lightContext = lightContext;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (lightContext == null) {
            System.out.println("The OmegaContext is not injected, The transaction handler is disabled");
        }
        registry.addInterceptor(new TransactionHandlerInterceptor(lightContext));
    }
}
