package com.empresa.emprestimos.controller;

import com.empresa.emprestimos.model.Colaborador;
import com.empresa.emprestimos.model.Emprestimo;

import com.empresa.emprestimos.service.ColaboradorService;
import com.empresa.emprestimos.service.EmprestimoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/colaboradores")
public class ColaboradorController {
    private final ColaboradorService service;
    private final EmprestimoService emprestimoService;

    public ColaboradorController(ColaboradorService service, EmprestimoService emprestimoService) {
        this.service = service;
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    public ResponseEntity<Colaborador> cadastrar(@Valid @RequestBody Colaborador colaborador) {
        Colaborador novo = service.cadastrar(colaborador);
        return ResponseEntity.status(201).body(novo);
    }

    @GetMapping
    public List<Colaborador> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Colaborador> buscarPorId(@PathVariable Long id) {
        return service.buscar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/emprestimos")
    public ResponseEntity<List<Emprestimo>> listarEmprestimos(@PathVariable Long id) {
        if (service.buscar(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Emprestimo> emprestimos = emprestimoService.listarDoColaborador(id);
        return ResponseEntity.ok(emprestimos);
    }
}
