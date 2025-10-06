package application.dto.usuario;


import application.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;

public record EditarUsuarioDTO(
        @NotBlank @Length(max = 150) @Email String email,
        @NotBlank @Length(max = 100) String nombre,
        @Length(max = 10) String telefono,
        @Length(max = 300) String fotoPerfil,
        @NotNull @Past LocalDate fechaNacimiento,
        @NotNull @Past Role rol,
        @NotNull @Past String contrasenia
) {

}