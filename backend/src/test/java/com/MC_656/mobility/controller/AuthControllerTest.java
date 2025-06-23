package com.MC_656.mobility.controller;

import com.MC_656.mobility.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Particionamento em classes de equivalência - Login")
    class EquivalencePartitioning {
        @Test
        void loginComDadosValidos_deveRetornar200() throws Exception {
            LoginRequest req = new LoginRequest();
            req.setEmail("user@email.com");
            req.setPassword("123456");
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());
        }
        @Test
        void loginComSenhaInvalida_deveRetornar401() throws Exception {
            LoginRequest req = new LoginRequest();
            req.setEmail("user@email.com");
            req.setPassword("senhaerrada");
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Análise de Valor Limite - Login")
    class BoundaryValueAnalysis {
        @Test
        void loginComSenhaMinima_deveRetornar401() throws Exception {
            LoginRequest req = new LoginRequest();
            req.setEmail("user@email.com");
            req.setPassword(""); // Limite inferior (vazio)
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }
        @Test
        void loginComSenhaMaxima_deveRetornar401() throws Exception {
            LoginRequest req = new LoginRequest();
            req.setEmail("user@email.com");
            req.setPassword("a".repeat(100)); // Limite superior (exemplo)
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }
    }
}
