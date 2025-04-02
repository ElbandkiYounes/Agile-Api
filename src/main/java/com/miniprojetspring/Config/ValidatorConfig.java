package com.miniprojetspring.Config;

import org.hibernate.validator.internal.constraintvalidators.bv.time.past.PastValidatorForLocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfig {

    @Bean
    public PastValidatorForLocalDate pastValidatorForLocalDate() {
        return new PastValidatorForLocalDate();
    }
}
