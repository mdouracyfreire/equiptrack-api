package com.empresa.emprestimos.controller;

import com.empresa.emprestimos.dto.LoginRequest;
import com.empresa.emprestimos.dto.TokenResposta;
import com.empresa.emprestimos.model.Usuario;
import com.empresa.emprestimos.repository.UsuarioRepository;
import com.empresa.emprestimos.security.TokenService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResposta> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository
                .findByUsuario(request.usuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos."));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos.");
        }

        String token = tokenService.gerar(usuario.getUsuario(), usuario.getPerfil().name());
        return ResponseEntity.ok(new TokenResposta(token));
    }
}
