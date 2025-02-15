package com.digital_money_house.security_service.utils;

import com.digital_money_house.security_service.dto.request.RegisterRequestDto;
import com.digital_money_house.security_service.exception.RequestValidationException;
import org.springframework.http.HttpStatus;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public static String generateCode() {
        return generateVerificationCode();
    }

    private static void normalizeFirstAndLastName(RegisterRequestDto request) {

        String initialFirstName = request.getFirstName().substring(0, 1);
        String restFirstName = request.getFirstName().substring(1);
        request.setFirstName(initialFirstName.toUpperCase() + restFirstName.toLowerCase());

        String initialLastName = request.getLastName().substring(0, 1);
        String restLastName = request.getLastName().substring(1);
        request.setLastName(initialLastName.toUpperCase() + restLastName.toLowerCase());

    }

    public static void processNameNormalization(RegisterRequestDto request) {
        normalizeFirstAndLastName(request);
    }

    public static boolean validateWord(String word) {
        String regex = "^[A-Za-zñÑáéíóúÁÉÍÓÚ ]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(word);

        if (matcher.matches()) {
            return true;
        } else {
            throw new RequestValidationException("El nombre o apellido no cumple con los valores especificados.", HttpStatus.BAD_REQUEST.value());
        }
    }

    public static boolean validateEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        if (matcher.matches()) {
            return true;
        } else {
            throw new RequestValidationException("El email no cumple con los valores especificados.", HttpStatus.BAD_REQUEST.value());
        }
    }


    public static Boolean validatePassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,12}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches()) {
            return true;
        } else {
            throw new RequestValidationException("La contraseña no cumple con los valores especificados.", HttpStatus.BAD_REQUEST.value());
        }
    }

    public static boolean validateDni(String dni) {
        String regex = "^[0-9]{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dni);

        if (matcher.matches()) {
            return true;
        } else {
            throw new RequestValidationException("El dni no cumple con los valores especificados.", HttpStatus.BAD_REQUEST.value());
        }
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        // Ajustar la longitud segun el formato que se espera (nacional/internacional)
        String regex = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);

        if (matcher.matches()) {
            return true;
        } else {
            throw new RequestValidationException("El número de teléfono no cumple con los valores especificados.", HttpStatus.BAD_REQUEST.value());
        }
    }

}
