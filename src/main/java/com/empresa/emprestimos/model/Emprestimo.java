package com.empresa.emprestimos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

import java.time.LocalDate;

@Entity
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Equipamento equipamento;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Colaborador colaborador;

    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;

    public Emprestimo() {}

    public Emprestimo(Equipamento equipamento, Colaborador colaborador, LocalDate hoje) {
        this.equipamento = equipamento;
        this.colaborador = colaborador;
        this.dataEmprestimo = hoje;
        this.dataPrevistaDevolucao = hoje.plusDays(14);
    }

    public void devolver(LocalDate data) {
        if (this.dataDevolucao != null) {
            throw new IllegalStateException("Este empréstimo já foi devolvido.");
        }

        this.dataDevolucao = data;
    }

    public boolean estaAtrasadoEm(LocalDate data) {
        return this.dataDevolucao == null
                && this.dataPrevistaDevolucao != null
                && this.dataPrevistaDevolucao.isBefore(data);
    }

    @Transient
    public boolean isAtrasado() {
        return estaAtrasadoEm(LocalDate.now());
    }

    public Long getId() {
        return id;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }
}
