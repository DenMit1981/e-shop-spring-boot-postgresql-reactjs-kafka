package com.training.eshop.config.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.modelmapper.ModelMapper;

@EnableKafka
@Configuration
public class SellerConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
