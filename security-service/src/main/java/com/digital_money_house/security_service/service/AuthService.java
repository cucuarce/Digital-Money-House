package com.digital_money_house.security_service.service;

import com.digital_money_house.security_service.config.jwt.JwtService;
import com.digital_money_house.security_service.dto.response.AuthResponseDto;
import com.digital_money_house.security_service.dto.request.LoginRequestDto;
import com.digital_money_house.security_service.dto.request.RegisterRequestDto;
import com.digital_money_house.security_service.entity.InvalidToken;
import com.digital_money_house.security_service.entity.Role;
import com.digital_money_house.security_service.entity.User;
import com.digital_money_house.security_service.exception.BadRequestException;
import com.digital_money_house.security_service.exception.ResourceAlreadyExistsException;
import com.digital_money_house.security_service.exception.ResourceNotFoundException;
import com.digital_money_house.security_service.repository.IInvalidTokenRepository;
import com.digital_money_house.security_service.repository.IUserRepository;
import com.digital_money_house.security_service.utils.ValidationUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
    private final IInvalidTokenRepository invalidTokenRepository;
    private final JwtService jwtService;
    @Value("${jwt.tokenExpirationSeconds}")
    private int jwtExpirationSeconds;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthResponseDto login(LoginRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe.", HttpStatus.NOT_FOUND.value()));

        if (!user.getEmail().equals(request.getEmail()) || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("El email o la contraseña no son correctas.", HttpStatus.BAD_REQUEST.value());
        }

        if (!user.isVerified()) {
            throw new BadRequestException("Cuenta no verificada. Por favor, confirme su correo.", HttpStatus.BAD_REQUEST.value());
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String token = jwtService.getToken(user);
        return AuthResponseDto.builder()
                .token(token)
                .build();
    }

    public void register(RegisterRequestDto request) throws MessagingException {

        ValidationUtils.processNameNormalization(request);

        ValidationUtils.validateWord(request.getFirstName());
        ValidationUtils.validateWord(request.getLastName());
        ValidationUtils.validateEmail(request.getEmail());
        ValidationUtils.validatePassword(request.getPassword());
        ValidationUtils.validateDni(request.getDni());
        ValidationUtils.validatePhoneNumber(request.getPhoneNumber());

        String verificationCode = ValidationUtils.generateCode();

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("El usuario ya existe", HttpStatus.CONFLICT.value());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .dni(request.getDni())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.USER)
                .verified(false)
                .verificationCode(verificationCode)
                .build();

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), user.getVerificationCode());

    }

    @Transactional
    public void logout(String token) {

        invalidTokenRepository.deleteExpiredTokens(LocalDateTime.now());

        if (invalidTokenRepository.existsByToken(token)) {
            throw new BadRequestException("El token ya está invalidado.", HttpStatus.BAD_REQUEST.value());
        }

        InvalidToken invalidToken = new InvalidToken();
        invalidToken.setToken(token);
        invalidToken.setInvalidatedAt(LocalDateTime.now());
        invalidToken.setExpiresAt(LocalDateTime.now().plusSeconds(jwtExpirationSeconds));
        invalidTokenRepository.save(invalidToken);
    }

    public void requestPasswordRecovery(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe.", HttpStatus.NOT_FOUND.value()));

        String recoveryLink = "http://localhost:8081/password/reset?email=" + email;
        emailService.sendPasswordRecoveryEmail(user.getEmail(), user.getUsername(), recoveryLink);
    }

    public void resetPassword(String email, String newPassword, String confirmPassword) {

        ValidationUtils.validatePassword(newPassword);

        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Las contraseñas no coinciden.", HttpStatus.BAD_REQUEST.value());
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe.", HttpStatus.NOT_FOUND.value()));

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException("La nueva contraseña no puede ser igual a la anterior.", HttpStatus.BAD_REQUEST.value());
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void verifyCode(String email, String verificationCode) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe.", HttpStatus.NOT_FOUND.value()));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(verificationCode)) {
            throw new BadRequestException("El código de verificación es incorrecto.", HttpStatus.BAD_REQUEST.value());
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        userRepository.save(user);

    }

}
