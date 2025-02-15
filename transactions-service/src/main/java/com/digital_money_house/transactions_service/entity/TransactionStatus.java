package com.digital_money_house.transactions_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum TransactionStatus {

    @JsonProperty("confirmed")
    CONFIRMED("CONFIRMED"),

    @JsonProperty("pending")
    PENDING("PENDING"),

    @JsonProperty("cancelled")
    CANCELLED("CANCELLED");

    private final String valor;

    TransactionStatus(String valor) {
        this.valor = valor;
    }
}

