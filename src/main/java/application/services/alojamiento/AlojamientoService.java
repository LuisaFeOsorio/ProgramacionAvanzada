package application.services.alojamiento;

import application.dto.alojamiento.AlojamientoDTO;
import application.dto.alojamiento.CrearAlojamientoDTO;
import application.dto.alojamiento.EditarAlojamientoDTO;
import application.dto.alojamiento.FiltroBusquedaDTO;
import application.dto.paginacion.PaginacionDTO;

import java.util.List;

public interface AlojamientoService {

    AlojamientoDTO crearAlojamiento(String hostId, CrearAlojamientoDTO dto);

    AlojamientoDTO obtenerAlojamiento(String alojamientoId);

    AlojamientoDTO editarAlojamiento(String alojamientoId, EditarAlojamientoDTO dto);

    void eliminarAlojamiento(String alojamientoId);

    List<AlojamientoDTO> listarAlojamientosAnfitrion(String hostId);

    PaginacionDTO<AlojamientoDTO> buscarAlojamientos(FiltroBusquedaDTO filtro);

    boolean puedeEliminarse(String alojamientoId);

    AlojamientoDTO marcarImagenPrincipal(String alojamientoId, String imageId);
}
