package com.empresa.emprestimos.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SegurancaIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    private String tokenAdmin;
    private String tokenColaborador;

    @BeforeEach
    void configurar() {
        tokenAdmin = tokenService.gerar("admin", "ADMIN");
        tokenColaborador = tokenService.gerar("colaborador", "COLABORADOR");
    }

    @Test
    void deveRetornar401QuandoNaoEnviarToken() throws Exception {
        mockMvc.perform(get("/equipamentos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoColaboradorTentarCadastrarEquipamento() throws Exception {
        String equipamentoJson = """
                {
                    "patrimonio": "SEG-001",
                    "nome": "Notebook de Segurança",
                    "tipo": "NOTEBOOK"
                }
                """;

        mockMvc.perform(post("/equipamentos")
                        .header("Authorization", "Bearer " + tokenColaborador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipamentoJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirCadastroDeEquipamentoParaAdministrador() throws Exception {
        String equipamentoJson = """
                {
                    "patrimonio": "SEG-002",
                    "nome": "Notebook Administrativo",
                    "tipo": "NOTEBOOK"
                }
                """;

        mockMvc.perform(post("/equipamentos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipamentoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.patrimonio").value("SEG-002"));
    }
}
