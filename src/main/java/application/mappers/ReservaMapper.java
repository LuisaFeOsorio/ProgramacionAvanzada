package application.mappers;

import application.dto.reserva.CrearReservaDTO;
import application.dto.reserva.ReservaDTO;
import application.model.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    // ✅ Mapeo para creación
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    @Mapping(target = "comentarios", ignore = true) // ✅ Agregar esto
    @Mapping(target = "serviciosExtras", source = "serviciosExtras")
    Reserva toEntity(CrearReservaDTO crearReservaDTO);

    // ✅ Mapeo para DTO completo - CORREGIDO
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nombre", target = "usuarioNombre")
    @Mapping(source = "usuario.email", target = "usuarioEmail")
    @Mapping(source = "alojamiento.id", target = "alojamientoId")
    @Mapping(source = "alojamiento.nombre", target = "alojamientoNombre")
    @Mapping(source = "alojamiento.direccion", target = "alojamientoDireccion")
    @Mapping(source = "alojamiento.precioPorNoche", target = "precioPorNoche")
    @Mapping(source = "alojamiento.anfitrion.id", target = "anfitrionId")
    @Mapping(source = "alojamiento.anfitrion.nombre", target = "anfitrionNombre")
    @Mapping(target = "tieneComentario", expression = "java(!reserva.getComentarios().isEmpty())") // ✅ CORREGIDO
    @Mapping(target = "calificacion", expression = "java(obtenerCalificacion(reserva))") // ✅ NUEVO método
    @Mapping(source = ".", target = "precioTotal", qualifiedByName = "calcularPrecioTotal")
    ReservaDTO toDTO(Reserva reserva);

    // ✅ Mapeo para actualización
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    void updateEntityFromDTO(CrearReservaDTO crearReservaDTO, @org.mapstruct.MappingTarget Reserva reserva);

    // ✅ MÉTODO PARA CALCULAR PRECIO TOTAL
    @Named("calcularPrecioTotal")
    default Double calcularPrecioTotal(Reserva reserva) {
        if (reserva.getCheckIn() == null || reserva.getCheckOut() == null ||
                reserva.getAlojamiento() == null || reserva.getAlojamiento().getPrecioPorNoche() == null) {
            return 0.0;
        }

        long noches = ChronoUnit.DAYS.between(reserva.getCheckIn(), reserva.getCheckOut());
        return reserva.getAlojamiento().getPrecioPorNoche() * noches;
    }

    // ✅ MÉTODO PARA OBTENER CALIFICACIÓN
    default Integer obtenerCalificacion(Reserva reserva) {
        if (reserva.getComentarios() == null || reserva.getComentarios().isEmpty()) {
            return null;
        }

        // Tomar la calificación del primer comentario (asumiendo 1 comentario por reserva)
        return reserva.getComentarios().stream()
                .findFirst()
                .map(comentario -> comentario.getCalificacion())
                .orElse(null);
    }
}