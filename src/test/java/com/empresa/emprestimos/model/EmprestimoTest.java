package com.empresa.emprestimos.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {
    private Emprestimo criarEmprestimo(LocalDate data) {
        Equipamento equipamento = new Equipamento();
        Colaborador colaborador = new Colaborador();

        return new Emprestimo(equipamento, colaborador, data);
    }

    @Test
    void deveCalcularPrazoDeQuatorzeDias() {
        LocalDate data = LocalDate.of(2026, 11, 14);
        Emprestimo emprestimo = criarEmprestimo(data);

        assertEquals(
                LocalDate.of(2026, 11, 14),
                emprestimo.getDataEmprestimo()
        );

        assertEquals(
                LocalDate.of(2026, 11, 28),
                emprestimo.getDataPrevistaDevolucao()
        );
    }

    @Test
    void deveRegistrarDataDeDevolucao() {
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 11, 14));
        LocalDate devolucao = LocalDate.of(2026, 11, 20);
        emprestimo.devolver(devolucao);

        assertEquals(devolucao, emprestimo.getDataDevolucao());
    }

    @Test
    void naoDevePermitirDevolucaoDuplicada() {
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 11, 14));
        emprestimo.devolver(LocalDate.of(2026, 11, 20));

        assertThrows(
                IllegalStateException.class,
                () -> emprestimo.devolver(LocalDate.of(2026, 11, 21))
        );

        assertEquals(
                LocalDate.of(2026, 11, 20),
                emprestimo.getDataDevolucao()
        );
    }

    @Test
    void deveIdentificarEmprestimoAtrasado() {
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 10, 27));
        boolean atrasado = emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14));

        assertTrue(atrasado);
    }

    @Test
    void naoDeveConsiderarAtrasadoAntesDoPrazo() {
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 11, 6));
        boolean atrasado = emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14));

        assertFalse(atrasado);
    }

    @Test
    void naoDeveConsiderarAtrasadoEmprestimoDevolvido() {
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 10, 27));
        emprestimo.devolver(LocalDate.of(2026, 11, 12));
        boolean atrasado = emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14));

        assertFalse(atrasado);
    }

    @Test
    void naoDeveConsiderarAtrasadoNoDiaDoPrazo() {
        Emprestimo emprestimo = criarEmprestimo(LocalDate.of(2026, 10, 31));
        boolean atrasado = emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14));

        assertFalse(atrasado);
    }

    @Test
    void deveAssociarEquipamentoEColaboradorAoEmprestimo() {
        Equipamento equipamento = new Equipamento();
        Colaborador colaborador = new Colaborador();
        Emprestimo emprestimo = new Emprestimo(equipamento, colaborador, LocalDate.of(2026, 11, 14));

        assertSame(equipamento, emprestimo.getEquipamento());
        assertSame(colaborador, emprestimo.getColaborador());
    }
}
