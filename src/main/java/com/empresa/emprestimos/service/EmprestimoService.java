package com.empresa.emprestimos.service;

import com.empresa.emprestimos.exception.ConflitoException;
import com.empresa.emprestimos.exception.NaoEncontradoException;

import com.empresa.emprestimos.model.Colaborador;
import com.empresa.emprestimos.model.Emprestimo;
import com.empresa.emprestimos.model.Equipamento;

import com.empresa.emprestimos.repository.ColaboradorRepository;
import com.empresa.emprestimos.repository.EmprestimoRepository;
import com.empresa.emprestimos.repository.EquipamentoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmprestimoService {
    private final EmprestimoRepository emprestimoRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final ColaboradorRepository colaboradorRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, EquipamentoRepository equipamentoRepository, ColaboradorRepository colaboradorRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.colaboradorRepository = colaboradorRepository;
    }

    public List<Emprestimo> listar(Boolean atrasados) {
        if (Boolean.TRUE.equals(atrasados)) {
            return emprestimoRepository
                    .findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate.now());
        }

        return emprestimoRepository.findAll();
    }

    public Optional<Emprestimo> buscar(Long id) {
        return emprestimoRepository.findById(id);
    }

    public List<Emprestimo> listarDoColaborador(Long colaboradorId) {
        if (!colaboradorRepository.existsById(colaboradorId)) {
            throw new NaoEncontradoException("Colaborador não encontrado.");
        }

        return emprestimoRepository.findByColaborador_Id(colaboradorId);
    }

    @Transactional
    public Emprestimo emprestar(Long equipamentoId, Long colaboradorId) {
        Equipamento equipamento = equipamentoRepository
                .findById(equipamentoId)
                .orElseThrow(() -> new NaoEncontradoException("Equipamento não encontrado."));

        Colaborador colaborador = colaboradorRepository
                .findById(colaboradorId)
                .orElseThrow(() -> new NaoEncontradoException("Colaborador não encontrado."));

        boolean possuiAtraso = emprestimoRepository
                .existsByColaborador_IdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(colaboradorId, LocalDate.now());

        if (possuiAtraso) {
            throw new ConflitoException("O colaborador possui empréstimos atrasados.");
        }

        equipamento.emprestar();
        Emprestimo emprestimo = new Emprestimo(equipamento, colaborador, LocalDate.now());
        equipamentoRepository.save(equipamento);

        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolver(Long id) {
        Emprestimo emprestimo = emprestimoRepository
                .findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Empréstimo não encontrado."));

        Equipamento equipamento = emprestimo.getEquipamento();
        emprestimo.devolver(LocalDate.now());
        equipamento.devolver();
        equipamentoRepository.save(equipamento);

        return emprestimoRepository.save(emprestimo);
    }
}
