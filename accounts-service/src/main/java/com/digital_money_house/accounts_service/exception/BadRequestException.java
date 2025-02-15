package com.digital_money_house.accounts_service.exception;

public class BadRequestException extends RuntimeException{

    private Integer statusCode;

    public BadRequestException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
