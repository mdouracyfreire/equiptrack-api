package com.empresa.emprestimos.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EquipamentoTest {
    @Test
    void deveEmprestarEquipamentoDisponivel() {
        Equipamento equipamento = new Equipamento();
        equipamento.setDisponivel(true);
        equipamento.emprestar();

        assertFalse(equipamento.isDisponivel());
    }

    @Test
    void naoDeveEmprestarEquipamentoIndisponivel() {
        Equipamento equipamento = new Equipamento();
        equipamento.setDisponivel(false);

        assertThrows(IllegalStateException.class, equipamento::emprestar);
        assertFalse(equipamento.isDisponivel());
    }

    @Test
    void deveDisponibilizarEquipamentoAposDevolucao() {
        Equipamento equipamento = new Equipamento();
        equipamento.setDisponivel(true);

        equipamento.emprestar();
        assertFalse(equipamento.isDisponivel());

        equipamento.devolver();
        assertTrue(equipamento.isDisponivel());
    }
}
