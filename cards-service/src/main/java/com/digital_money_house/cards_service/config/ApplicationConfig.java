package com.digital_money_house.cards_service.config;

import com.digital_money_house.cards_service.dto.response.UserClientDto;
import com.digital_money_house.cards_service.exception.ResourceNotFoundException;
import com.digital_money_house.cards_service.feign.UserClient;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@Configuration
public class ApplicationConfig {

    private final UserClient userClient;

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
                    //.password(userResponseDto.getPassword())
                    .password("{noop}dummyPassword")
                    .authorities(Collections.emptyList())
                    .build();
        };
    }
}
