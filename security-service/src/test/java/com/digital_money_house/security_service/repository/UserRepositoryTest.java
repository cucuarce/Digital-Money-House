package com.digital_money_house.security_service.repository;

import com.digital_money_house.security_service.entity.Role;
import com.digital_money_house.security_service.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRepositoryTest {

    @Autowired
    private IUserRepository userRepository;

    @Test
    public void testFindByEmail_UserExists() {
        // Arrange: Crear y guardar un usuario en la base de datos
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("tests1!");
        user.setFirstName("User");
        user.setLastName("Test");
        user.setRole(Role.USER);
        user.setDni("12345678");
        user.setPhoneNumber("1234567890");

        userRepository.save(user);

        // Act: Buscar usuario por email
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Assert: Verificar que el usuario fue encontrado
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    public void testFindByEmail_UserDoesNotExist() {
        // Act: Intentar buscar un email que no existe en la base de datos
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert: Verificar que no se encontró ningún usuario
        assertFalse(foundUser.isPresent());
    }
}

