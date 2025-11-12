package application.controllers;

import application.dto.ResponseDTO;
import application.dto.auth.LoginDTO;
import application.model.Usuario;
import application.repositories.UsuarioRepository;
import application.security.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtil;
    private final UsuarioRepository usuarioRepository;


    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> login(@RequestBody LoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.contrasenia())
            );

            Usuario usuario = findByEmail(loginDTO.email());

            Map<String, String> claims = new HashMap<>();
            claims.put("id", String.valueOf(usuario.getId()));
            claims.put("role", usuario.getRol().name());
            claims.put("email", usuario.getEmail());

            String token = jwtUtil.generateToken(usuario.getEmail(), claims);

            return ResponseEntity.ok(new ResponseDTO<>(false, "Login exitoso", token));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>(true, "Credenciales inválidas", null));
        }
    }



    // Endpoint para desarrollo - crear usuario de prueba
    @PostMapping("/create-test-user")
    public ResponseEntity<String> createTestUser() {

        return ResponseEntity.ok("Endpoint para crear usuario de prueba");
    }

    // Endpoint opcional para verificar token
    @PostMapping("/verify")
    public ResponseEntity<ResponseDTO<Boolean>> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            boolean isValid = jwtUtil.validateToken(jwt);
            return ResponseEntity.ok(new ResponseDTO<>(false, "Token verificado", isValid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>(true, "Token inválido", false));
        }
    }

    // Mantén el endpoint original por si acaso (puedes eliminarlo después)
    @PostMapping("/login-legacy")
    public String loginLegacy(@RequestParam String username, @RequestParam String password) {
        return "Método legacy - Usuario: " + username;
    }
}