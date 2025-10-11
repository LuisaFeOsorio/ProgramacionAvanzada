package application.controllers;

import application.dto.paginacion.PaginacionDTO;
import application.dto.reserva.CrearReservaDTO;
import application.dto.reserva.FiltroReservaDTO;
import application.dto.reserva.ReservaDTO;
import application.dto.usuario.UsuarioDTO;
import application.exceptions.reserva.ReservaNoCanceladaException;
import application.exceptions.reserva.ReservaNoCreadaException;
import application.exceptions.reserva.ReservasNoObtenidasException;
import application.model.Usuario;
import application.services.impl.UsuarioServiceImpl;
import application.services.reserva.ReservaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioServiceImpl usuarioService;

    public ReservaController(ReservaService reservaService, UsuarioServiceImpl usuarioService) {
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> crearReserva(
            Authentication authentication,
            @Valid @RequestBody CrearReservaDTO dto) throws ReservaNoCreadaException {

        System.out.println("🏨 === CREAR RESERVA ===");
        System.out.println("Authentication: " + authentication);

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ReservaNoCreadaException("Usuario no autenticado");
        }

        String email = authentication.getName();
        System.out.println("✅ Usuario autenticado: " + email);

        // Buscar el ID del usuario por email
        String usuarioId = obtenerUsuarioIdPorEmail(email);

        ReservaDTO reserva = reservaService.crearReserva(usuarioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }
    private String obtenerUsuarioIdPorEmail(String email) throws ReservaNoCreadaException {
        try {
            System.out.println("🔍 Buscando usuario por email: " + email);

            // Opción 1: Si tienes UsuarioService
            UsuarioDTO usuario = usuarioService.obtenerPorEmail(email);
            System.out.println("✅ Usuario encontrado: " + usuario.nombre() + " - ID: " + usuario.id());
            return usuario.id().toString();

        } catch (Exception e) {
            System.out.println("❌ Error obteniendo ID de usuario: " + e.getMessage());
            throw new ReservaNoCreadaException("Usuario no encontrado con email: " + email);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtenerReserva(@PathVariable String id) throws ReservasNoObtenidasException {
        ReservaDTO reserva = reservaService.obtenerReserva(id);
        return ResponseEntity.ok(reserva);
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarReserva(
            @PathVariable String id,
            @AuthenticationPrincipal Usuario usuario) throws ReservaNoCanceladaException {
        reservaService.cancelarReserva(id, usuario.getId().toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PaginacionDTO<ReservaDTO>> listarReservas(
            @ModelAttribute FiltroReservaDTO filtro) {
        PaginacionDTO<ReservaDTO> reservas = reservaService.listarReservas(filtro);
        return ResponseEntity.ok(reservas);
    }

    @PostMapping("/{id}/aprobar")
    public ResponseEntity<ReservaDTO> aprobarReserva(
            @PathVariable String id,
            @AuthenticationPrincipal Usuario anfitrion) {
        ReservaDTO reserva = reservaService.aprobarReserva(id, anfitrion.getId().toString());
        return ResponseEntity.ok(reserva);
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<ReservaDTO> rechazarReserva(
            @PathVariable String id,
            @AuthenticationPrincipal Usuario anfitrion) {
        ReservaDTO reserva = reservaService.rechazarReserva(id, anfitrion.getId().toString());
        return ResponseEntity.ok(reserva);
    }
}