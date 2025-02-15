package com.digital_money_house.accounts_service.exception;

import com.digital_money_house.accounts_service.dto.JsonMessageDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(annotations = RestController.class)
public class HandlerException {

    private static final Logger logger = LogManager.getLogger(HandlerException.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex){
        ex.printStackTrace();
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(HandlerException.getError(ex.getMessage(), ex.getStatusCode()));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<?> resourceAlreadyExistsException(ResourceAlreadyExistsException ex){
        ex.printStackTrace();
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(HandlerException.getError(ex.getMessage(), ex.getStatusCode()));
    }

    @ExceptionHandler(IllegalDateException.class)
    public ResponseEntity<?> illegalDateException(IllegalDateException ex){
        ex.printStackTrace();
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HandlerException.getError(ex.getMessage(), ex.getStatusCode()));
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<?> requestValidationException(RequestValidationException ex){
        ex.printStackTrace();
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HandlerException.getError(ex.getMessage(), ex.getStatusCode()));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<?> internalServerError(InternalServerErrorException ex){
        ex.printStackTrace();
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(HandlerException.getError(ex.getMessage(), ex.getStatusCode()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestException(BadRequestException ex) {
        ex.printStackTrace();
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HandlerException.getError(ex.getMessage(), ex.getStatusCode()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<?> insufficientFundsException(InsufficientFundsException ex) {
        ex.printStackTrace();
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE).body(HandlerException.getError(ex.getMessage(), ex.getStatusCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();

            errors.put(fieldName, message);
        });
        return errors;
    }

    private static JsonMessageDto getError(String message, Integer status){

        return new JsonMessageDto(message, status);
    }

}

