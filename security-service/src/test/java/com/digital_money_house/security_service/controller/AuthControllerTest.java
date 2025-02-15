package com.digital_money_house.security_service.controller;

import com.digital_money_house.security_service.config.jwt.JwtService;
import com.digital_money_house.security_service.dto.request.*;
import com.digital_money_house.security_service.dto.response.AuthResponseDto;
import com.digital_money_house.security_service.repository.IInvalidTokenRepository;
import com.digital_money_house.security_service.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)  // Desactiva los filtros de seguridad en los tests
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private IInvalidTokenRepository invalidTokenRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequestDto loginRequest;
    private RegisterRequestDto registerRequest;
    private PasswordRecoveryRequestDto recoveryRequest;
    private PasswordResetRequestDto resetRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequestDto("test@example.com", "Tests1!");
        registerRequest = new RegisterRequestDto("John", "1234567890", "Doe", "test@example.com", "Tests1!", "12345678");
        recoveryRequest = new PasswordRecoveryRequestDto();
        resetRequest = new PasswordResetRequestDto();
    }

    @Test
    void testLogin_Success() throws Exception {
        AuthResponseDto mockResponse = new AuthResponseDto("mockToken");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDto("test@example.com", "Tests1!"))))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
    }

    @Test
    void testRegister_Success() throws Exception {
        doNothing().when(authService).register(any(RegisterRequestDto.class));

        mockMvc.perform(post("/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Nuevo usuario registrado, revisa tu email para confirmar la cuenta."));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testLogout_Success() throws Exception {
        String authHeader = "Bearer mockToken";
        doNothing().when(authService).logout("mockToken");

        mockMvc.perform(post("/users/auth/logout")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout exitoso. Token invalidado."));
    }

    @Test
    void testLogout_InvalidTokenFormat() throws Exception {
        mockMvc.perform(post("/users/auth/logout")
                        .header("Authorization", "InvalidTokenFormat"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Formato de token inválido."));
    }

    @Test
    void testVerifyAccount_Success() throws Exception {
        registerRequest.setVerificationCode("123456");
        doNothing().when(authService).verifyCode(anyString(), anyString());

        mockMvc.perform(post("/users/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("La verificación de la cuenta ha sido exitosa."));
    }

    @Test
    void testVerifyAccount_MissingEmail() throws Exception {
        registerRequest.setEmail(null);
        registerRequest.setVerificationCode("123456");

        mockMvc.perform(post("/users/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El email es obligatorio."));
    }

    @Test
    void testVerifyAccount_MissingVerificationCode() throws Exception {
        registerRequest.setVerificationCode(null);

        mockMvc.perform(post("/users/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El código de verificación es obligatorio."));
    }

    @Test
    void testRequestPasswordRecovery_Success() throws Exception {
        doNothing().when(authService).requestPasswordRecovery(anyString());

        mockMvc.perform(post("/users/auth/password/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recoveryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Correo de recuperación enviado."));
    }

    @Test
    void testResetPassword_Success() throws Exception {
        doNothing().when(authService).resetPassword(anyString(), anyString(), anyString());

        mockMvc.perform(post("/users/auth/password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contraseña actualizada correctamente."));
    }
}

