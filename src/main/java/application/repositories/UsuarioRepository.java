package application.repositories;


import application.model.Usuario;
import application.model.enums.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByNombreContaining(String nombre, Pageable pageable);


    List<Usuario> findByEmailContaining(String email, Pageable pageable);


    List<Usuario> findByNombreContainingAndEmailContaining(String nombre, String email, Pageable pageable);


    List<Usuario> findByRol(Role rol);

    List<Usuario> findByRolAndDocumentosVerificadosTrue(Role rol);
}

