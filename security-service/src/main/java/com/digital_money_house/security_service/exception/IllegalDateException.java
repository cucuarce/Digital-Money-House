package com.digital_money_house.security_service.exception;

public class IllegalDateException extends RuntimeException{
    private Integer statusCode;

    public IllegalDateException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
