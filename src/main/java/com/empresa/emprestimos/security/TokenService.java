package com.empresa.emprestimos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class TokenService {
    private static final Duration VALIDADE_TOKEN = Duration.ofHours(2);
    private final SecretKey chave;

    public TokenService(@Value("${jwt.segredo}") String segredo) {
        if (segredo == null || segredo.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("O segredo JWT deve possuir pelo menos 32 bytes.");
        }

        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
    }

    public String gerar(String usuario, String perfil) {
        Instant agora = Instant.now();

        return Jwts.builder()
                .subject(usuario)
                .claim("perfil", perfil)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(VALIDADE_TOKEN)))
                .signWith(chave)
                .compact();
    }

    public Claims ler(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean valido(String token) {
        try {
            ler(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
