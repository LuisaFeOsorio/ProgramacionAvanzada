package application.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record VolverseAnfitrionDTO(
        @NotBlank @Length(max = 500) String descripcionPersonal,
        @NotBlank @Length(max = 100) String documentoIdentidad,
        @NotBlank @Length(max = 200) String archivoDocumentos
) {
}
