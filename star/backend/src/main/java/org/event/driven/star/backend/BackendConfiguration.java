package org.event.driven.star.backend;

import org.event.driven.star.backend.service.BackendService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
public class BackendConfiguration {

    @Bean
    public BackendService backendService() {
        return new BackendService();
    }

}
