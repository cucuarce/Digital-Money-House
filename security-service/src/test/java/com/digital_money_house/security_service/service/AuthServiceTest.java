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
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IInvalidTokenRepository invalidTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("Tests1!")
                .firstName("John")
                .lastName("Doe")
                .dni("12345678")
                .phoneNumber("1234567890")
                .role(Role.USER)
                .verified(true)
                .verificationCode("123456")
                .build();
    }

    @Test
    void testLogin_Success() {
        LoginRequestDto request = new LoginRequestDto("test@example.com", "Tests1!");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtService.getToken(testUser)).thenReturn("mocked_token");

        AuthResponseDto response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked_token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_UserNotFound() {
        LoginRequestDto request = new LoginRequestDto("wrong@example.com", "Tests1!");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void testLogin_IncorrectPassword() {
        LoginRequestDto request = new LoginRequestDto("test@example.com", "wrongpassword");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(request.getPassword(), testUser.getPassword())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(request));
    }

    @Test
    void testLogin_UserNotVerified() {
        testUser.setVerified(false);
        LoginRequestDto request = new LoginRequestDto("test@example.com", "Tests1!");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(BadRequestException.class, () -> authService.login(request));
    }

    @Test
    void testRegister_Success() throws MessagingException {
        RegisterRequestDto request = new RegisterRequestDto("John", "1234567890", "Doe", "test@example.com", "Tests1!", "12345678");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        authService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testRegister_UserAlreadyExists() {
        RegisterRequestDto request = new RegisterRequestDto("John", "1234567890", "Doe", "test@example.com", "Tests1!", "12345678");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(testUser));

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(request));
    }

    @Test
    void testLogout_Success() {
        String token = "valid_token";
        when(invalidTokenRepository.existsByToken(token)).thenReturn(false);

        authService.logout(token);

        verify(invalidTokenRepository, times(1)).save(any(InvalidToken.class));
    }

    @Test
    void testLogout_TokenAlreadyInvalid() {
        String token = "invalid_token";
        when(invalidTokenRepository.existsByToken(token)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.logout(token));
    }

    @Test
    void testRequestPasswordRecovery_Success() throws MessagingException {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        authService.requestPasswordRecovery("test@example.com");

        verify(emailService, times(1)).sendPasswordRecoveryEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testRequestPasswordRecovery_UserNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.requestPasswordRecovery("nonexistent@example.com"));
    }

    @Test
    void testResetPassword_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedNewPassword");

        authService.resetPassword("test@example.com", "Tests2!", "Tests2!");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testResetPassword_SameAsOld() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Tests1!", testUser.getPassword())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.resetPassword("test@example.com", "Tests1!", "Tests1!"));
    }

    @Test
    void testVerifyCode_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        authService.verifyCode("test@example.com", "123456");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyCode_InvalidCode() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        assertThrows(BadRequestException.class, () -> authService.verifyCode("test@example.com", "wrongCode"));
    }
}

