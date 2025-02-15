package com.digital_money_house.security_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum Role {

    @JsonProperty("admin")
    ADMIN("ADMIN"),

    @JsonProperty("user")
    USER("USER");

    private final String valor;

    Role(String valor) {
        this.valor = valor;
    }
}
