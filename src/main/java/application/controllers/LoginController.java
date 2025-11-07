package application.controllers;

import application.dto.auth.LoginDTO;
import application.dto.ResponseDTO;
import application.security.JWTUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<ResponseDTO<String>> login(@RequestBody LoginDTO loginDTO) {
        try {
            System.out.println("🔐 === LOGIN ATTEMPT ===");
            System.out.println("Email: " + loginDTO.email());
            System.out.println("Contraseña: " + loginDTO.contrasenia());

            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.contrasenia())
            );

            System.out.println("✅ === AUTHENTICATION SUCCESSFUL ===");

            // Generar claims y token
            Map<String, String> claims = new HashMap<>();
            claims.put("email", loginDTO.email());
            claims.put("role", "ANFITRION"); // Ajusta según el rol real

            String token = jwtUtil.generateToken(loginDTO.email(), claims);
            System.out.println("🎉 TOKEN CREADO Y ENVIADO");

            ResponseDTO<String> response = new ResponseDTO<>(false, "Login exitoso", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ === LOGIN FAILED ===");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
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