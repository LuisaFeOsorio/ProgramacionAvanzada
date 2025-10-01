package application.dto.usuario;

import application.model.enumm.Role;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record UsuarioDTO(

        @NotBlank
        @Length(max = 100) String nombre,

        @NotBlank
        @Length(max = 150) @Email String email,

        @Length(max = 50) String telefono,

        @NotBlank @Length(min = 7, max = 100) String contrasenia,

        @Length(max = 300) String fotoPerfil,

        @NotNull @Past LocalDate fechaNacimiento,

        @NotNull Role rol

) {}

