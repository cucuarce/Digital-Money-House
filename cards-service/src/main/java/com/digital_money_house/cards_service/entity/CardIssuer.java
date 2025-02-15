package com.digital_money_house.cards_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum CardIssuer {

    @JsonProperty("visa")
    VISA("VISA"),

    @JsonProperty("mastercard")
    MASTERCARD("MASTERCARD"),

    @JsonProperty("amex")
    AMEX("AMEX");

    private final String valor;

    CardIssuer(String valor) {
        this.valor = valor;
    }
}
