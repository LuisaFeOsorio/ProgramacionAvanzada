package application.dto.usuario;

import org.hibernate.validator.constraints.Length;

public record ActualizarAnfitrionDTO(
        @Length(max = 500) String descripcionPersonal,
        @Length(max = 100) String documentoIdentidad,
        @Length(max = 200) String archivoDocumentos
) {}
