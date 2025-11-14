package application.controllers;

import application.dto.ResponseDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioServiceImpl usuarioService;

    public ReservaController(ReservaService reservaService, UsuarioServiceImpl usuarioService) {
        this.reservaService = reservaService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/crear")
    public ResponseEntity<ResponseDTO<ReservaDTO>> crearReserva(
            @Valid @RequestBody CrearReservaDTO dto) {

        System.out.println("🏨 === CREAR RESERVA ===");
        System.out.println("📦 DTO recibido: " + dto.toString());

        try {

            System.out.println("🔍 Validando usuario ID: " + dto.usuarioId());
            Usuario usuario = usuarioService.findById(dto.usuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.usuarioId()));

            System.out.println("✅ Usuario validado: " + usuario.getNombre());

            ReservaDTO reserva = reservaService.crearReserva(dto);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseDTO<>(
                            false,
                            "Reserva creada correctamente",
                            reserva
                    ));

        } catch (Exception e) {
            System.out.println("❌ Error creando reserva: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO<>(
                            true,
                            "Error al crear reserva: " + e.getMessage(),
                            null
                    ));
        }
    }


    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaDTO>> getMisReservas(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            System.out.println("🔍 === OBTENER MIS RESERVAS ===");
            System.out.println("👤 Usuario autenticado (username): " + userDetails.getUsername());

            String username = userDetails.getUsername();

            // Convertir el username (que es el ID del token) a Long
            Long userId = Long.parseLong(username);

            // Verificar que el usuario exista (por seguridad)
            Usuario usuario = usuarioService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

            System.out.println("✅ Usuario encontrado: " + usuario.getId() + " - " + usuario.getEmail());

            // Buscar reservas asociadas al usuario directamente por ID
            List<ReservaDTO> reservas = reservaService.findByUsuarioId(usuario.getId());

            System.out.println("📦 Reservas encontradas: " + reservas.size());
            return ResponseEntity.ok(reservas);

        } catch (Exception e) {
            System.out.println("❌ Error obteniendo reservas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    private String obtenerUsuarioIdPorEmail(String email) throws ReservaNoCreadaException {
        try {
            System.out.println("🔍 Buscando usuario por email: " + email);

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

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaDTO> cancelarReserva(@PathVariable Long id) {
        System.out.println("🛑 Cancelando reserva con ID: " + id);
        try {
            ReservaDTO reservaCancelada = reservaService.cancelarReserva(id);
            return ResponseEntity.ok(reservaCancelada);
        } catch (Exception e) {
            System.out.println("❌ Error cancelando reserva: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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