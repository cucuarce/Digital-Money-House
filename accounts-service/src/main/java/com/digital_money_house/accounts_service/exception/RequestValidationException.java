package com.digital_money_house.accounts_service.exception;

public class RequestValidationException extends RuntimeException{

    private Integer statusCode;

    public RequestValidationException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
