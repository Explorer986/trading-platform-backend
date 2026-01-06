package com.jatin.trading.trading_platform_backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class UserDetailsConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        // Explicitly define UserDetailsService so Spring does NOT create a default one
        return username -> {
            throw new RuntimeException("User not found");
        };
    }
}
