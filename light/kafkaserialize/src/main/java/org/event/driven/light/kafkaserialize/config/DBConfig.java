package org.event.driven.light.kafkaserialize.config;

import org.event.driven.light.kafkaserialize.dbconnection.DBHelper;
import org.event.driven.light.kafkaserialize.dbconnection.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfig {

    @Bean
    DBHelper dbHelper(@Value("${spring.datasource.url}")String url) {
        return new DBHelper(url);
    }

    @Bean
    MessageService messageService(DBHelper dbHelper){
        return new MessageService(dbHelper);
    }
}
