package com.digital_money_house.cards_service.utils;

import com.digital_money_house.cards_service.exception.RequestValidationException;
import org.springframework.http.HttpStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z ]+$");
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^\\d{16}$");
    private static final Pattern EXPIRATION_DATE_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/[0-9]{2}$");

    public static String normalizeCardHolderName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del titular no puede estar vacío.");
        }
        name = name.toUpperCase().trim();
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("El nombre del titular solo puede contener letras y espacios.");
        }
        return name;
    }

    public static void validateCardNumber(String cardNumber) {
        if (cardNumber == null || !CARD_NUMBER_PATTERN.matcher(cardNumber).matches()) {
            throw new RequestValidationException("El número de tarjeta debe contener exactamente 16 dígitos.", HttpStatus.BAD_REQUEST.value());
        }
    }

    public static void validateExpirationDate(String expirationDate) {
        if (expirationDate == null || !EXPIRATION_DATE_PATTERN.matcher(expirationDate).matches()) {
            throw new RequestValidationException("La fecha de expiración debe estar en formato MM/YY.", HttpStatus.BAD_REQUEST.value());
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
            dateFormat.setLenient(false);
            dateFormat.parse(expirationDate);
        } catch (ParseException e) {
            throw new RequestValidationException("Fecha de expiración inválida.", HttpStatus.BAD_REQUEST.value());
        }
    }
}
