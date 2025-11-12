package application.mappers;

import application.dto.reserva.CrearReservaDTO;
import application.dto.reserva.ReservaDTO;
import application.model.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    // ✅ Versión MÍNIMA - solo propiedades que SÍ existen
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "precioTotal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    @Mapping(target = "serviciosExtras", source = "serviciosExtras")
    Reserva toEntity(CrearReservaDTO crearReservaDTO);

    // ✅ Solo mapea las propiedades que DEFINITIVAMENTE existen
    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "alojamiento.id", target = "alojamientoId")
    @Mapping(source = "alojamiento.nombre", target = "alojamientoNombre")
    @Mapping(source = "precioTotal", target = "precioTotal")
    @Mapping(source = "fechaCreacion", target = "checkIn")
    ReservaDTO toDTO(Reserva reserva);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "precioTotal", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "alojamiento", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    @Mapping(target = "serviciosExtras", source = "serviciosExtras")
    void updateEntityFromDTO(CrearReservaDTO crearReservaDTO, @org.mapstruct.MappingTarget Reserva reserva);
}