package com.digital_money_house.security_service.controller;

import com.digital_money_house.security_service.dto.*;
import com.digital_money_house.security_service.dto.request.LoginRequestDto;
import com.digital_money_house.security_service.dto.request.PasswordRecoveryRequestDto;
import com.digital_money_house.security_service.dto.request.PasswordResetRequestDto;
import com.digital_money_house.security_service.dto.request.RegisterRequestDto;
import com.digital_money_house.security_service.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*") // Comentar para que me permita las solicitudes desde el gateway al front
@RestController
@RequestMapping("/users/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registrar(@RequestBody @Valid RegisterRequestDto request) throws MessagingException {
        authService.register(request);
        return new ResponseEntity<>(new JsonMessageDto("Nuevo usuario registrado, revisa tu email para confirmar la cuenta.", HttpStatus.CREATED.value()), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    @Secured({ "ADMIN", "USER" })
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(new JsonMessageDto("Formato de token inválido.", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        authService.logout(token);
        return new ResponseEntity<>(new JsonMessageDto("Logout exitoso. Token invalidado.", HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyAccount(@RequestBody RegisterRequestDto request) {
        String email = request.getEmail();
        String verificationCode = request.getVerificationCode();

        if (email == null || email.isBlank()) {
            return new ResponseEntity<>(new JsonMessageDto("El email es obligatorio.", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
        if (verificationCode == null || verificationCode.isBlank()) {
            return new ResponseEntity<>(new JsonMessageDto("El código de verificación es obligatorio.", HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }

        authService.verifyCode(email, verificationCode);
        return new ResponseEntity<>(new JsonMessageDto("La verificación de la cuenta ha sido exitosa.", HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PostMapping("/password/recovery")
    public ResponseEntity<?> requestPasswordRecovery(@RequestBody PasswordRecoveryRequestDto request) throws MessagingException {
        authService.requestPasswordRecovery(request.getEmail());
        return new ResponseEntity<>(new JsonMessageDto("Correo de recuperación enviado.", HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequestDto request) {
        authService.resetPassword(request.getEmail(), request.getNewPassword(), request.getConfirmPassword());
        return new ResponseEntity<>(new JsonMessageDto("Contraseña actualizada correctamente.", HttpStatus.OK.value()), HttpStatus.OK);
    }

}


