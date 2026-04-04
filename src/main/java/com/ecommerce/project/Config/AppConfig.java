package com.ecommerce.project.Config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    /**
     * Creates a shared ModelMapper bean for DTO/entity mapping.
     *
     * @return model mapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
