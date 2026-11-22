package com.empresa.emprestimos.service;

import com.empresa.emprestimos.exception.ConflitoException;
import com.empresa.emprestimos.model.Colaborador;
import com.empresa.emprestimos.repository.ColaboradorRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColaboradorService {
    private final ColaboradorRepository repository;

    public ColaboradorService(ColaboradorRepository repository) {
        this.repository = repository;
    }

    public List<Colaborador> listar() {
        return repository.findAll();
    }

    public Optional<Colaborador> buscar(Long id) {
        return repository.findById(id);
    }

    public Colaborador cadastrar(Colaborador colaborador) {
        if (repository.existsByEmail(colaborador.getEmail())) {
            throw new ConflitoException("Já existe um colaborador cadastrado com este e-mail.");
        }

        return repository.save(colaborador);
    }
}
