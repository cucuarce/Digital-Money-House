package com.digital_money_house.cards_service.exception;

public class InternalServerErrorException extends RuntimeException{

    private Integer statusCode;

    public InternalServerErrorException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
