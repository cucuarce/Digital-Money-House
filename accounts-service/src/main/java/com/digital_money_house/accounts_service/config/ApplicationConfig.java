package com.digital_money_house.accounts_service.config;

import com.digital_money_house.accounts_service.dto.response.UserClientDto;
import com.digital_money_house.accounts_service.exception.ResourceNotFoundException;
import com.digital_money_house.accounts_service.feign.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Configuration
public class ApplicationConfig {

    private final UserClient userClient;

    @Autowired
    public ApplicationConfig(UserClient userClient) {
        this.userClient = userClient;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailService() {
        return email -> {
            UserClientDto userClientDto = userClient.getUserByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email, HttpStatus.NOT_FOUND.value()));

            return User.withUsername(userClientDto.getEmail())
                    //.password(userResponseDto.getPassword()) // Suponiendo que `password` ya está encriptada
                    .password("{noop}dummyPassword")
                    .authorities(Collections.emptyList()) // Si tienes roles, agrégalos aquí
                    .build();
        };
    }
}
