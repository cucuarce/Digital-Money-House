package com.digital_money_house.security_service.controller;

import com.digital_money_house.security_service.config.jwt.JwtService;
import com.digital_money_house.security_service.dto.request.RegisterRequestDto;
import com.digital_money_house.security_service.dto.response.UserResponseDto;
import com.digital_money_house.security_service.entity.Role;
import com.digital_money_house.security_service.repository.IInvalidTokenRepository;
import com.digital_money_house.security_service.service.impl.UserService;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private IInvalidTokenRepository invalidTokenRepository;

    private UserResponseDto mockUser;
    private RegisterRequestDto registerRequestDto;

    @BeforeEach
    void setUp() {
        mockUser = new UserResponseDto(1L, "test@example.com", "John", "Doe", "12345678", "1234567890", true, Role.USER);
        registerRequestDto = new RegisterRequestDto("John", "1234567890", "Doe", "test@example.com", "Tests1!", "12345678");
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetUserById_Success() throws Exception {
        when(userService.findById(1L)).thenReturn(mockUser);

        mockMvc.perform(get("/users/api/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUser.getId()))
                .andExpect(jsonPath("$.email").value(mockUser.getEmail()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testGetUserByEmail_Success() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);

        mockMvc.perform(get("/users/api/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(mockUser.getEmail()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testDeleteUserById_Success() throws Exception {
        doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/users/api/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario eliminado exitosamente."));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testListUsers_Success() throws Exception {
        when(userService.listAll()).thenReturn(List.of(mockUser));

        mockMvc.perform(get("/users/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].email").value(mockUser.getEmail()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testListUsers_EmptyList() throws Exception {
        when(userService.listAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/api"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testUpdateUser_Success() throws Exception {
        doNothing().when(userService).update(any(RegisterRequestDto.class));

        mockMvc.perform(put("/users/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Usuario actualizado exitosamente."));
    }

}

