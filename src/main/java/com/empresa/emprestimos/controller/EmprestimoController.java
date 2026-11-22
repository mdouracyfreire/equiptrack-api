package com.empresa.emprestimos.controller;

import com.empresa.emprestimos.model.Emprestimo;
import com.empresa.emprestimos.service.EmprestimoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {
    private final EmprestimoService service;

    public EmprestimoController(EmprestimoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Emprestimo> emprestar(@RequestParam Long equipamentoId, @RequestParam Long colaboradorId) {
        Emprestimo novo = service.emprestar(equipamentoId, colaboradorId);
        return ResponseEntity.status(201).body(novo);
    }

    @GetMapping
    public List<Emprestimo> listar(@RequestParam(required = false) Boolean atrasados) {
        return service.listar(atrasados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emprestimo> buscarPorId(@PathVariable Long id) {
        return service.buscar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/devolucao")
    public ResponseEntity<Emprestimo> devolver(@PathVariable Long id) {
        Emprestimo atualizado = service.devolver(id);
        return ResponseEntity.ok(atualizado);
    }
}
