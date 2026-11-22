package com.empresa.emprestimos.controller;

import com.empresa.emprestimos.model.Colaborador;
import com.empresa.emprestimos.model.Emprestimo;
import com.empresa.emprestimos.model.Equipamento;
import com.empresa.emprestimos.service.EmprestimoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmprestimoControllerUnitTest {
    private MockMvc mockMvc;
    private EmprestimoService service;

    @BeforeEach
    void configurar() {
        service = mock(EmprestimoService.class);
        EmprestimoController controller = new EmprestimoController(service);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private Emprestimo criarEmprestimo(LocalDate data) {
        Equipamento equipamento = new Equipamento();
        Colaborador colaborador = new Colaborador();

        return new Emprestimo(equipamento, colaborador, data);
    }

    @Test
    void deveCriarControllerComServiceSimulado() {
        EmprestimoController controller = new EmprestimoController(service);

        assertNotNull(controller);
    }

    @Test
    void deveListarTodosOsEmprestimos() throws Exception {
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 11, 22));

        when(service.listar(null)).thenReturn(List.of(emprestimo));

        mockMvc.perform(get("/emprestimos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].equipamento").exists())
                .andExpect(jsonPath("$[0].colaborador").exists());

        verify(service).listar(null);
    }

    @Test
    void deveRetornarStatus200AoListarEmprestimos() throws Exception {
        when(service.listar(null)).thenReturn(List.of());

        mockMvc.perform(get("/emprestimos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deveCadastrarEmprestimoComSucesso() throws Exception {
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 11, 22));

        when(service.emprestar(1L, 2L)).thenReturn(emprestimo);

        mockMvc.perform(post("/emprestimos")
                        .param("equipamentoId", "1")
                        .param("colaboradorId", "2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.equipamento").exists())
                .andExpect(jsonPath("$.colaborador").exists());

        verify(service).emprestar(1L, 2L);
    }

    @Test
    void deveRejeitarEmprestimoSemParametros() throws Exception {
        mockMvc.perform(post("/emprestimos")).andExpect(status().isBadRequest());

        verify(service, never()).emprestar(anyLong(), anyLong());
    }

    @Test
    void deveRetornar404QuandoEmprestimoNaoExistir() throws Exception {
        Long idInexistente = 999L;

        when(service.buscar(idInexistente)).thenReturn(Optional.empty());

        mockMvc.perform(get("/emprestimos/{id}", idInexistente))
                .andExpect(status().isNotFound());

        verify(service).buscar(idInexistente);
    }

    @Test
    void deveBuscarEmprestimoExistente() throws Exception {
        Long id = 1L;
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 11, 22));

        when(service.buscar(id)).thenReturn(Optional.of(emprestimo));

        mockMvc.perform(get("/emprestimos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.equipamento").exists())
                .andExpect(jsonPath("$.colaborador").exists());

        verify(service).buscar(id);
    }

    @Test
    void deveRegistrarDevolucaoComSucesso() throws Exception {
        Long id = 1L;
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 11, 22));
        emprestimo.devolver(LocalDate.of(2026, 11, 23));

        when(service.devolver(id)).thenReturn(emprestimo);

        mockMvc.perform(put("/emprestimos/{id}/devolucao", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataDevolucao")
                        .value("2026-11-23"));

        verify(service).devolver(id);
    }

    @Test
    void deveListarSomenteEmprestimosAtrasados() throws Exception {
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 11, 1));

        when(service.listar(true)).thenReturn(List.of(emprestimo));

        mockMvc.perform(get("/emprestimos")
                        .param("atrasados", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].atrasado").value(true));

        verify(service).listar(true);
    }
}
