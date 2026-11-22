package com.empresa.emprestimos.config;

import com.empresa.emprestimos.model.Perfil;
import com.empresa.emprestimos.model.Usuario;
import com.empresa.emprestimos.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InicializadorUsuarios {
    @Bean
    public CommandLineRunner inicializarUsuarios(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, @Value("${app.admin.senha:}") String senhaAdmin, @Value("${app.colaborador.senha:}") String senhaColaborador) {
        return args -> {
            if (!senhaAdmin.isBlank() && usuarioRepository.findByUsuario("admin").isEmpty()) {
                Usuario admin = new Usuario("admin", passwordEncoder.encode(senhaAdmin), Perfil.ADMIN);

                usuarioRepository.save(admin);
                System.out.println("Usuário administrador inicializado.");
            }

            if (!senhaColaborador.isBlank() && usuarioRepository.findByUsuario("colaborador").isEmpty()) {
                Usuario colaborador = new Usuario("colaborador", passwordEncoder.encode(senhaColaborador), Perfil.COLABORADOR);

                usuarioRepository.save(colaborador);
                System.out.println("Usuário colaborador inicializado.");
            }
        };
    }
}
