package application.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;

public record EditarUsuarioDTO(
        @NotBlank @Length(max = 100) String name,
        @Length(max = 10) String phone,
        @Length(max = 300) String photoUrl,
        @NotNull @Past LocalDate dateBirth
) {
}