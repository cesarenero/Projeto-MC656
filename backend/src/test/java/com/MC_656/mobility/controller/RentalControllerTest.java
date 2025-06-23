package com.MC_656.mobility.controller;

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
public class RentalControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String getToken() {
        // Retorne um JWT válido para autenticação, ou mock conforme necessário
        return "Bearer <token_aqui>";
    }

    @Nested
    @DisplayName("Particionamento em classes de equivalência - Início de aluguel")
    class EquivalencePartitioning {
        @Test
        void iniciarAluguelComVeiculoDisponivel_deveRetornar201() throws Exception {
            String body = "{\"vehicleId\":1}";
            mockMvc.perform(post("/api/rentals/start")
                    .header("Authorization", getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isCreated());
        }
        @Test
        void iniciarAluguelComVeiculoInexistente_deveRetornar404() throws Exception {
            String body = "{\"vehicleId\":99999}";
            mockMvc.perform(post("/api/rentals/start")
                    .header("Authorization", getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Análise de Valor Limite - Início de aluguel")
    class BoundaryValueAnalysis {
        @Test
        void iniciarAluguelComIdZero_deveRetornar404() throws Exception {
            String body = "{\"vehicleId\":0}";
            mockMvc.perform(post("/api/rentals/start")
                    .header("Authorization", getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isNotFound());
        }
        @Test
        void iniciarAluguelComIdNegativo_deveRetornar404() throws Exception {
            String body = "{\"vehicleId\":-1}";
            mockMvc.perform(post("/api/rentals/start")
                    .header("Authorization", getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isNotFound());
        }
    }
}
