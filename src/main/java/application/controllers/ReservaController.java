package application.controllers;

import application.dto.paginacion.PaginacionDTO;
import application.dto.reserva.CrearReservaDTO;
import application.dto.reserva.FiltroReservaDTO;
import application.dto.reserva.ReservaDTO;
import application.exceptions.reserva.ReservaNoCanceladaException;
import application.exceptions.reserva.ReservaNoCreadaException;
import application.exceptions.reserva.ReservasNoObtenidasException;
import application.model.Usuario;
import application.services.reserva.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> crearReserva(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody CrearReservaDTO dto) throws ReservaNoCreadaException {
        ReservaDTO reserva = reservaService.crearReserva(usuario.getId().toString(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
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