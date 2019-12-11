package org.event.driven.star.customer;

import org.event.driven.star.customer.service.CustomerService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
public class CustomerConfiguration {

    @Bean
    public CustomerService customerService() {
        return new CustomerService();
    }


}
