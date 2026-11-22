package com.empresa.emprestimos.controller;

import com.empresa.emprestimos.repository.EquipamentoRepository;
import com.empresa.emprestimos.security.TokenService;

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
class EquipamentoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private TokenService tokenService;

    private String tokenAdmin;

    @BeforeEach
    void configurar() {
        tokenAdmin = tokenService.gerar("admin", "ADMIN");
    }

    @Test
    void deveCadastrarEquipamentoComSucesso() throws Exception {
        String equipamentoJson = """
                {
                    "patrimonio": "TESTE-EQ-001",
                    "nome": "Notebook de Integração",
                    "tipo": "NOTEBOOK"
                }
                """;

        mockMvc.perform(post("/equipamentos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipamentoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.patrimonio").value("TESTE-EQ-001"))
                .andExpect(jsonPath("$.nome").value("Notebook de Integração"))
                .andExpect(jsonPath("$.tipo").value("NOTEBOOK"))
                .andExpect(jsonPath("$.disponivel").value(true));
    }

    @Test
    void deveRetornar404QuandoEquipamentoNaoExistir() throws Exception {
        mockMvc.perform(get("/equipamentos/{id}", 999999L)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRejeitarEquipamentoComDadosInvalidos() throws Exception {
        String equipamentoJson = """
                {
                    "patrimonio": "",
                    "nome": "",
                    "tipo": null
                }
                """;

        mockMvc.perform(post("/equipamentos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipamentoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erros.patrimonio").exists())
                .andExpect(jsonPath("$.erros.nome").exists())
                .andExpect(jsonPath("$.erros.tipo").exists());
    }

    @Test
    void deveRejeitarPatrimonioDuplicado() throws Exception {
        String equipamentoJson = """
                {
                    "patrimonio": "TESTE-EQ-DUPLICADO",
                    "nome": "Notebook Dell",
                    "tipo": "NOTEBOOK"
                }
                """;

        mockMvc.perform(post("/equipamentos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipamentoJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/equipamentos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(equipamentoJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro")
                        .value("Já existe um equipamento com este patrimônio."));
    }
}
