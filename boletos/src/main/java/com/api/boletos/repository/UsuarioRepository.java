package com.api.boletos.repository;

import com.api.boletos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Este método mágico buscará a un usuario por su correo electrónico para el Login
    Optional<Usuario> findByEmail(String email);
}