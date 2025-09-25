package application.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginDTO(
    @NotBlank @Email String correo,
    @NotBlank @Length(min = 6, max = 50) String contrasena){
}
