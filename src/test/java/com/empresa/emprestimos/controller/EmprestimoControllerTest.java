package com.empresa.emprestimos.controller;

import com.empresa.emprestimos.model.Colaborador;
import com.empresa.emprestimos.model.Equipamento;
import com.empresa.emprestimos.model.TipoEquipamento;
import com.empresa.emprestimos.repository.ColaboradorRepository;
import com.empresa.emprestimos.repository.EquipamentoRepository;
import com.empresa.emprestimos.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmprestimoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private TokenService tokenService;

    private Equipamento equipamento;
    private Colaborador colaborador;
    private String tokenAdmin;

    @BeforeEach
    void configurar() {
        tokenAdmin = tokenService.gerar("admin", "ADMIN");
        equipamento = new Equipamento();
        equipamento.setPatrimonio("TESTE-" + UUID.randomUUID());
        equipamento.setNome("Notebook de Integração");
        equipamento.setTipo(TipoEquipamento.NOTEBOOK);
        equipamento.setDisponivel(true);

        equipamento = equipamentoRepository.save(equipamento);

        colaborador = new Colaborador();
        colaborador.setNome("Ana Oliveira");
        colaborador.setEmail("ana-" + UUID.randomUUID() + "@teste.com");
        colaborador.setCargo("Desenvolvedora");

        colaborador = colaboradorRepository.save(colaborador);
    }

    @Test
    void deveRealizarEmprestimoComSucesso() throws Exception {
        mockMvc.perform(post("/emprestimos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("equipamentoId", equipamento.getId().toString())
                        .param("colaboradorId", colaborador.getId().toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.equipamento.id").value(equipamento.getId()))
                .andExpect(jsonPath("$.colaborador.id").value(colaborador.getId()))
                .andExpect(jsonPath("$.equipamento.disponivel").value(false))
                .andExpect(jsonPath("$.dataPrevistaDevolucao").exists());
    }

    @Test
    void deveRegistrarDevolucaoComSucesso() throws Exception {
        String resposta = mockMvc.perform(post("/emprestimos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("equipamentoId", equipamento.getId().toString())
                        .param("colaboradorId", colaborador.getId().toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        Long emprestimoId = mapper.readTree(resposta).get("id").asLong();

        mockMvc.perform(put("/emprestimos/{id}/devolucao", emprestimoId)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataDevolucao").exists())
                .andExpect(jsonPath("$.equipamento.disponivel").value(true));

        mockMvc.perform(get("/equipamentos/{id}", equipamento.getId())
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponivel").value(true));
    }

    @Test
    void deveRejeitarEmprestimoDeEquipamentoIndisponivel() throws Exception {
        mockMvc.perform(post("/emprestimos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("equipamentoId", equipamento.getId().toString())
                        .param("colaboradorId", colaborador.getId().toString()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/emprestimos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("equipamentoId", equipamento.getId().toString())
                        .param("colaboradorId", colaborador.getId().toString()))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar404QuandoEmprestimoNaoExistir() throws Exception {
        mockMvc.perform(get("/emprestimos/{id}", 999999L)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }
}
