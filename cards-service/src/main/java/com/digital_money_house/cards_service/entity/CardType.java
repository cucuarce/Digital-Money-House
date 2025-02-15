package com.digital_money_house.cards_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum CardType {

    @JsonProperty("debit")
    DEBIT("DEBIT"),

    @JsonProperty("credit")
    CREDIT("CREDIT");

    private final String valor;

    CardType(String valor) {
        this.valor = valor;
    }

}
