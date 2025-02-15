package com.digital_money_house.transactions_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum TransactionType {

    @JsonProperty("income")
    INCOME("INCOME"),

    @JsonProperty("expense")
    EXPENSE("EXPENSE"),

    @JsonProperty("transfer")
    TRANSFER("TRANSFER");

    private final String valor;

    TransactionType(String valor) {
        this.valor = valor;
    }
}

