package com.empresa.emprestimos.service;

import com.empresa.emprestimos.exception.ConflitoException;
import com.empresa.emprestimos.exception.NaoEncontradoException;

import com.empresa.emprestimos.model.Equipamento;
import com.empresa.emprestimos.model.TipoEquipamento;

import com.empresa.emprestimos.repository.EquipamentoRepository;
import com.empresa.emprestimos.repository.EmprestimoRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipamentoService {
    private final EquipamentoRepository repository;
    private final EmprestimoRepository emprestimoRepository;

    public EquipamentoService(EquipamentoRepository repository, EmprestimoRepository emprestimoRepository) {
        this.repository = repository;
        this.emprestimoRepository = emprestimoRepository;
    }

    public Equipamento cadastrar(Equipamento equipamento) {
        if (repository.existsByPatrimonio(equipamento.getPatrimonio())) {
            throw new ConflitoException("Já existe um equipamento com este patrimônio.");
        }

        equipamento.setId(null);
        equipamento.setDisponivel(true);
        return repository.save(equipamento);
    }

    public List<Equipamento> listarTodos() {
        return repository.findAll();
    }

    public Optional<Equipamento> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Equipamento> filtrar(TipoEquipamento tipo, Boolean disponivel) {
        if (tipo != null && disponivel != null) {
            return repository.findByTipoAndDisponivel(tipo, disponivel);
        }

        if (tipo != null) {
            return repository.findByTipo(tipo);
        }

        if (disponivel != null) {
            return repository.findByDisponivel(disponivel);
        }

        return repository.findAll();
    }

    public Equipamento atualizar(Long id, Equipamento dadosAtualizados) {
        Equipamento equipamento = repository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Equipamento não encontrado."));

        equipamento.setNome(dadosAtualizados.getNome());
        equipamento.setTipo(dadosAtualizados.getTipo());

        return repository.save(equipamento);
    }

    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new NaoEncontradoException("Equipamento não encontrado.");
        }

        if (emprestimoRepository.existsByEquipamento_Id(id)) {
            throw new ConflitoException("Não é possível excluir um equipamento com histórico de empréstimos.");
        }

        repository.deleteById(id);
    }
}
