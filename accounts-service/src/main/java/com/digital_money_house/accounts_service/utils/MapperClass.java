package com.digital_money_house.accounts_service.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class MapperClass {

    public static ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();

        // Registrar módulo de Java 8 para soporte de LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        // Deshabilitar el uso de timestamps para fechas y forzar formato String
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }
}
