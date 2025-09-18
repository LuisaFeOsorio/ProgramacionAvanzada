package application.services;

import application.dto.*;

import java.util.List;

public interface AlojamientoService {
    /** Crea un alojamiento y lo asocia al anfitrión (hostId). */
    AlojamientoDTO crearAlojamiento(String hostId, CrearAlojamientoDTO dto);

    /** Obtiene detalle completo (galería, calendario disponibilidad, comentarios). */
    AlojamientoDTO obtenerAlojamiento(String alojamientoId);

    /** Edita alojamiento (solo propietario). */
    AlojamientoDTO editarAlojamiento(String alojamientoId, EditarAlojamientoDTO dto);

    /** Soft-delete: marcar como ELIMINADO sólo si no hay reservas futuras. */
    void eliminarAlojamiento(String alojamientoId);

    /** Lista alojamientos de un anfitrión (con filtros opcionales). */
    List<AlojamientoDTO> listarAlojamientosAnfitrion(String hostId);

    /** Lista pública de alojamientos (aplica paginación y filtros). */
    PaginacionDTO<AlojamientoDTO> buscarAlojamientos(FiltroBusquedaDTO filtro);

    /** Comprueba si el alojamiento puede eliminarse (sin reservas futuras). */
    boolean puedeEliminarse(String alojamientoId);

    /** Marcar imagen principal, añadir / eliminar imágenes (delegable a ImagenService). */
    AlojamientoDTO marcarImagenPrincipal(String alojamientoId, String imageId);
}
