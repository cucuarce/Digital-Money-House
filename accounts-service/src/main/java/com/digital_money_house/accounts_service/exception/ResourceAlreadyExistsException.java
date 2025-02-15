package com.digital_money_house.accounts_service.exception;

public class ResourceAlreadyExistsException extends RuntimeException{
    private Integer statusCode;
    public ResourceAlreadyExistsException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
