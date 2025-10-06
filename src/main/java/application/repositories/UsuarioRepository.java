package application.repositories;


import application.model.entidades.Usuario;
import application.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

        boolean existsByEmail(String email);
        Optional<Usuario> findByEmail(String email);

        List<Usuario> findByNombreContaining(String nombre, Pageable pageable);
        List<Usuario> findByEmailContaining(String email, Pageable pageable);
        List<Usuario> findByNombreContainingAndEmailContaining(String nombre, String email, Pageable pageable);

        List<Usuario> findByRol(Role rol);
        List<Usuario> findByRolAndDocumentosVerificadosTrue(Role rol);

//        // Buscar anfitriones activos y verificados
//        @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND u.activo = true AND u.documentosVerificados = true")
//        List<Usuario> findAnfitrionesActivosYVerificados(@Param("rol") Role rol);
    }


