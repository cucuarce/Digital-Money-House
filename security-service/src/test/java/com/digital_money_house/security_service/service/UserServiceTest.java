package com.digital_money_house.security_service.service;

import com.digital_money_house.security_service.dto.request.RegisterRequestDto;
import com.digital_money_house.security_service.dto.response.UserResponseDto;
import com.digital_money_house.security_service.entity.Role;
import com.digital_money_house.security_service.entity.User;
import com.digital_money_house.security_service.exception.ResourceNotFoundException;
import com.digital_money_house.security_service.repository.IUserRepository;
import com.digital_money_house.security_service.service.impl.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;

    private RegisterRequestDto registerRequestDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("test@example.com");
        testUser.setPassword("Tests1!");
        testUser.setPhoneNumber("1234567890");
        testUser.setDni("12345678");
        testUser.setRole(Role.USER);

        registerRequestDto = new RegisterRequestDto("John", "1234567890", "Doe", "test@example.com", "Tests1!", "12345678");
        registerRequestDto.setId(1L); // 🔹 Asegurar que no sea null en `update()`
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponseDto result = userService.findById(1L);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testFindByEmail_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserResponseDto result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testFindByEmail_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByEmail("test@example.com"));
    }

    @Test
    void testUpdateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.update(registerRequestDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.update(registerRequestDto));
    }

    @Test
    void testDeleteById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteById(1L);

        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void testDeleteById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(1L));
    }

    @Test
    void testListAllUsers_Success() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserResponseDto> users = userService.listAll();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    void testListAllUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponseDto> users = userService.listAll();

        assertNotNull(users);
        assertTrue(users.isEmpty());
        verify(userRepository, times(1)).findAll();
        verify(objectMapper, never()).convertValue(any(User.class), eq(UserResponseDto.class));
    }
}

