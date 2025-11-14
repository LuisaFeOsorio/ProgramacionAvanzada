package application.dto.usuario;


import application.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;

        public record EditarUsuarioDTO(
        @Length(max = 100)
                String nombre,
        String email,

        @Length(max = 50)
        String telefono,

        @Length(min = 7, max = 100)  // Solo validar longitud si se envía
        String contrasenia,

        String foto_perfil,

        @Past
        LocalDate fechaNacimiento,

        Role rol
) {

}

