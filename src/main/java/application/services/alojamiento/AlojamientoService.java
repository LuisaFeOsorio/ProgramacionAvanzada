package application.services.alojamiento;

import application.dto.alojamiento.AlojamientoDTO;
import application.dto.alojamiento.CrearAlojamientoDTO;
import application.dto.alojamiento.EditarAlojamientoDTO;
import application.dto.alojamiento.FiltroBusquedaDTO;
import application.dto.paginacion.PaginacionDTO;
import application.exceptions.alojamiento.*;
import application.model.Alojamiento;
import application.repositories.AlojamientoRepository;

import java.util.List;

public interface AlojamientoService {

    AlojamientoDTO crear(CrearAlojamientoDTO dto);

    List<AlojamientoDTO> obtenerTodos() ;

    AlojamientoDTO mapToDTO(Alojamiento a);

    AlojamientoDTO crearAlojamiento(String hostId, CrearAlojamientoDTO dto) throws CrearAlojamientoException;

    AlojamientoDTO obtenerAlojamiento(String alojamientoId) throws ObtenerAlojamientoException;

    AlojamientoDTO editarAlojamiento(String alojamientoId, EditarAlojamientoDTO dto) throws EditarAlojamientoException;

    void eliminarAlojamiento(String alojamientoId) throws EliminarAlojamientoException;

    List<AlojamientoDTO> listarAlojamientosAnfitrion(String hostId) throws ListarAlojamientosException;

    PaginacionDTO<AlojamientoDTO> buscarAlojamientos(FiltroBusquedaDTO filtro) throws BuscarAlojamientoException;

    boolean puedeEliminarse(String alojamientoId);

    AlojamientoDTO marcarImagenPrincipal(String alojamientoId, String imageId);
    List<AlojamientoDTO> obtenerTodosAlojamientos();


    AlojamientoDTO obtenerAlojamientoPorId(Long id);
}
