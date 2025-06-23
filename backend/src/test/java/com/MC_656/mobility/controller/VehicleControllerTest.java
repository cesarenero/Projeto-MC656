package com.MC_656.mobility.controller;

import com.MC_656.mobility.dto.VehicleRequest;
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
public class VehicleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String getToken() {
        // Retorne um JWT válido para autenticação, ou mock conforme necessário
        return "Bearer <token_aqui>";
    }

    @Nested
    @DisplayName("Particionamento em classes de equivalência - Criação de veículo")
    class EquivalencePartitioning {
        @Test
        void criarVeiculoComDadosValidos_deveRetornar201() throws Exception {
            VehicleRequest req = new VehicleRequest();
            req.setMake("Caloi");
            req.setModel("Explorer");
            req.setYearManufacture(2024);
            req.setLicensePlate("EQP-0001");
            req.setType("E_BIKE");
            req.setLatitude(-22.8);
            req.setLongitude(-47.0);
            mockMvc.perform(post("/api/vehicles")
                    .header("Authorization", getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }
        @Test
        void criarVeiculoComPlacaDuplicada_deveRetornar400() throws Exception {
            VehicleRequest req = new VehicleRequest();
            req.setMake("Caloi");
            req.setModel("Explorer");
            req.setYearManufacture(2024);
            req.setLicensePlate("EQP-0001"); // Placa já cadastrada
            req.setType("E_BIKE");
            req.setLatitude(-22.8);
            req.setLongitude(-47.0);
            mockMvc.perform(post("/api/vehicles")
                    .header("Authorization", getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Análise de Valor Limite - Criação de veículo")
    class BoundaryValueAnalysis {
        @Test
        void criarVeiculoComAnoMinimo_deveRetornar201() throws Exception {
            VehicleRequest req = new VehicleRequest();
            req.setMake("Caloi");
            req.setModel("Explorer");
            req.setYearManufacture(1900); // Limite inferior
            req.setLicensePlate("EQP-0002");
            req.setType("E_BIKE");
            req.setLatitude(-22.8);
            req.setLongitude(-47.0);
            mockMvc.perform(post("/api/vehicles")
                    .header("Authorization", getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }
        @Test
        void criarVeiculoComAnoMaximo_deveRetornar201() throws Exception {
            VehicleRequest req = new VehicleRequest();
            req.setMake("Caloi");
            req.setModel("Explorer");
            req.setYearManufacture(2100); // Limite superior
            req.setLicensePlate("EQP-0003");
            req.setType("E_BIKE");
            req.setLatitude(-22.8);
            req.setLongitude(-47.0);
            mockMvc.perform(post("/api/vehicles")
                    .header("Authorization", getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }
    }
}
