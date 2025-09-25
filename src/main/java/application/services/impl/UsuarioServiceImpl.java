package application.services.impl;

import application.model.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioServiceImpl
        extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);
}
