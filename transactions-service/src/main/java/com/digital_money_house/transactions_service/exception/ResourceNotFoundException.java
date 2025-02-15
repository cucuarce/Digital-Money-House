package com.digital_money_house.transactions_service.exception;

public class ResourceNotFoundException extends RuntimeException{
    private Integer statusCode;
    public ResourceNotFoundException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
