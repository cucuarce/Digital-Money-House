package com.digital_money_house.security_service.integration;

import com.digital_money_house.security_service.entity.InvalidToken;
import com.digital_money_house.security_service.entity.User;
import com.digital_money_house.security_service.repository.IInvalidTokenRepository;
import com.digital_money_house.security_service.repository.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IInvalidTokenRepository invalidTokenRepository;

    @Autowired
    private IUserRepository userRepository;

    private String jwtToken;

    @BeforeEach
    @Transactional
    @Rollback(value = true)
    void setUp(TestInfo testInfo) throws Exception {

        invalidTokenRepository.deleteAll();
        userRepository.deleteAll();

        if (!testInfo.getDisplayName().equals("testAccessProtectedEndpointWithInvalidToken")) {
            registerUser();
            verifyUser();
            jwtToken = getJwtToken();
        }
    }

    private void registerUser() throws Exception {
        mockMvc.perform(post("/users/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "test@example.com",
                            "password": "Tests1!",
                            "dni": "12345678",
                            "phoneNumber": "1234567890"
                        }
                    """))
                .andExpect(status().isCreated());
    }

    private void verifyUser() throws Exception {
        User savedUser = userRepository.findByEmail("test@example.com").orElseThrow();
        String verificationCode = savedUser.getVerificationCode();

        mockMvc.perform(post("/users/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "test@example.com",
                            "verificationCode": "%s"
                        }
                    """.formatted(verificationCode)))
                .andExpect(status().isOk());
    }

    private String getJwtToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/users/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "test@example.com",
                            "password": "Tests1!"
                        }
                    """))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void testUserRegistrationAndLogin() throws Exception {
        assertNotNull(jwtToken, "El token JWT no debe ser nulo después de un login exitoso.");
    }

    @Test
    void testAccessProtectedEndpointWithValidToken() throws Exception {
        User savedUser = userRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new IllegalStateException("El usuario no fue encontrado en la base de datos"));

        Long userId = savedUser.getId();

        mockMvc.perform(get("/users/api/" + userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void testAccessProtectedEndpointWithInvalidToken() throws Exception {
        String validToken = getJwtToken();

        invalidTokenRepository.save(new InvalidToken(validToken));

        mockMvc.perform(get("/users/api/1")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testLogoutInvalidatesToken() throws Exception {
        mockMvc.perform(post("/users/auth/logout")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout exitoso. Token invalidado."));

        assertTrue(invalidTokenRepository.existsByToken(jwtToken));
    }
}

