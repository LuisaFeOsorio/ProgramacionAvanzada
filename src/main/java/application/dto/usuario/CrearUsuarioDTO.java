package application.dto.usuario;

import application.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;

public record CrearUsuarioDTO(


        @NotBlank @Length(max = 100) String nombre,
        @NotBlank @Length(max = 150) @Email String email,
        @Length(max = 50) String telefono,
        @NotBlank @Length(min = 7, max = 100) String contrasenia,
        @Length(max = 300) String fotoPerfil,
        @Past LocalDate fechaNacimiento,
        Role rol


) {
    @Override
    public String toString() {
        return "CrearUsuarioDTO{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                '}';
    }
}
