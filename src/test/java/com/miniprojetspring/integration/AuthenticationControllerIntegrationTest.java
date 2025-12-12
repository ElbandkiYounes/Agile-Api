package com.miniprojetspring.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniprojetspring.payload.LoginUserPayload;
import com.miniprojetspring.payload.RegisterUserPayload;
import com.miniprojetspring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void signupThenLogin_returnsJwtToken() throws Exception {
        RegisterUserPayload registerPayload = RegisterUserPayload.builder()
                .fullName("Jane ProductOwner")
                .email("jane@example.com")
                .password("Secret123!")
                .build();

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(registerPayload.getEmail()))
                .andExpect(jsonPath("$.fullName").value(registerPayload.getFullName()));

        LoginUserPayload loginPayload = LoginUserPayload.builder()
                .email(registerPayload.getEmail())
                .password(registerPayload.getPassword())
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").value(3600000));
    }
}
