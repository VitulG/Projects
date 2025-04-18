package com.rghousing.realestate.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class Config {

    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> {
                    try {
                        requests
                                .anyRequest()
                                .permitAll()
                                .and().cors().disable()
                                .csrf().disable();
                    }catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
        return http.build();
    }
}
