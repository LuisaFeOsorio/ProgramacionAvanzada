// application/dto/reserva/ReservaDTO.java
package application.dto.reserva;

import application.model.enums.EstadoReserva;
import application.model.enums.Servicio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReservaDTO {
    @NotBlank @Length(max = 100) private String id;

    @NotNull private LocalDate checkIn;

    @NotNull private LocalDate checkOut;

    @NotNull @Positive private Integer numeroHuespedes;

    @NotNull private EstadoReserva estado;

    @NotNull private List<Servicio> serviciosExtras;

    // Información del usuario
    @NotBlank @Length(max = 100) private String usuarioId;

    @NotBlank @Length(max = 100) private String usuarioNombre;

    @NotBlank @Length(max = 100) private String usuarioEmail;

    // Información del alojamiento
    @NotBlank @Length(max = 100) private String alojamientoId;

    @NotBlank @Length(max = 100) private String alojamientoNombre;

    @NotBlank @Length(max = 255) private String alojamientoDireccion;

    @NotNull @Positive private Double precioPorNoche;

    @NotNull @Positive private Double precioTotal;

    // Información del anfitrión
    @NotBlank @Length(max = 100) private String anfitrionId;

    @NotBlank @Length(max = 100) private String anfitrionNombre;

    // Información de comentarios
    @NotNull private Boolean tieneComentario;

    @Positive private Integer calificacion;
}