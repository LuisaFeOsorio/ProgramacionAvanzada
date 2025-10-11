package application.controllers;

import application.dto.auth.LoginDTO;
import application.dto.ResponseDTO;
import application.security.JWTUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> login(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("🔐 === LOGIN ATTEMPT - RAW BODY ===");
            System.out.println("Request Body: " + requestBody);

            // Extraer manualmente los campos
            String email = (String) requestBody.get("email");
            String contrasenia = (String) requestBody.get("contrasenia");

            System.out.println("Email extraído: " + email);
            System.out.println("Contraseña extraída: " + contrasenia);

            if (email == null || contrasenia == null) {
                System.out.println("❌ Campos faltantes en JSON");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO<>(true, "Email y contraseña son requeridos", null));
            }

            // Validación manual
            if (email.isBlank() || contrasenia.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseDTO<>(true, "Email y contraseña no pueden estar vacíos", null));
            }

            // Resto de la lógica de autenticación...
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, contrasenia)
            );

            System.out.println("✅ === AUTHENTICATION SUCCESSFUL ===");

            // Generar claims
            Map<String, String> claims = new HashMap<>();
            claims.put("email", email);
            claims.put("role", "USER");

            // Generar token JWT
            String token = jwtUtil.generateToken(email, claims);

            return ResponseEntity.ok(new ResponseDTO<>(false, "Login exitoso", token));

        } catch (Exception e) {
            System.out.println("❌ === LOGIN FAILED ===");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO<>(true, "Credenciales inválidas", null));
        }
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