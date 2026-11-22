package com.empresa.emprestimos;

import com.empresa.emprestimos.model.Colaborador;
import com.empresa.emprestimos.model.Emprestimo;
import com.empresa.emprestimos.model.Equipamento;
import com.empresa.emprestimos.model.TipoEquipamento;

import com.empresa.emprestimos.repository.ColaboradorRepository;
import com.empresa.emprestimos.repository.EmprestimoRepository;
import com.empresa.emprestimos.repository.EquipamentoRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EmprestimoRelacionamentoIntegrationTest {
    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Test
    void devePersistirRelacionamentosEntreEntidades() {
        Equipamento equipamento = new Equipamento();
        equipamento.setPatrimonio("TESTE-REL-001");
        equipamento.setNome("Notebook de Teste");
        equipamento.setTipo(TipoEquipamento.NOTEBOOK);
        equipamento.setDisponivel(true);

        equipamento = equipamentoRepository.save(equipamento);

        Colaborador colaborador = new Colaborador();
        colaborador.setNome("Colaborador de Teste");
        colaborador.setEmail("relacionamento@teste.com");
        colaborador.setCargo("Desenvolvedor");

        colaborador = colaboradorRepository.save(colaborador);

        Long equipamentoId = equipamento.getId();
        Long colaboradorId = colaborador.getId();

        Emprestimo emprestimo = new Emprestimo(equipamento, colaborador, LocalDate.of(2026, 11, 22));
        emprestimo = emprestimoRepository.saveAndFlush(emprestimo);
        Long emprestimoId = emprestimo.getId();
        emprestimoRepository.flush();

        Emprestimo encontrado = emprestimoRepository.findById(emprestimoId).orElseThrow();

        assertEquals(equipamentoId, encontrado.getEquipamento().getId());
        assertEquals(colaboradorId, encontrado.getColaborador().getId());
        assertTrue(emprestimoRepository.existsByEquipamento_Id(equipamentoId));
        assertEquals(1, emprestimoRepository.findByColaborador_Id(colaboradorId).size());
    }
}
