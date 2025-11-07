package application.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Rutas que NO pasan por el filtro JWT
        return (path.equals("/api/usuarios") && "POST".equals(method)) ||
                (path.equals("/api/usuarios/registro") && "POST".equals(method)) ||
                path.startsWith("/api/auth/") ||
                path.startsWith("/api/dev/") || path.startsWith("/api/contrasenia/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("=== JWT FILTER - Ruta protegida ===");
        System.out.println("Path: " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No Bearer token found");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": true, \"mensaje\": \"Token de autorización requerido\"}");
            return;
        }

        try {
            String jwt = authHeader.substring(7);

            if (jwtUtil.validateToken(jwt)) {
                var claims = jwtUtil.extractClaims(jwt);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                System.out.println("✅ Token válido - Email: " + email);

                var authToken = new UsernamePasswordAuthenticationToken(email, null, null);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                filterChain.doFilter(request, response);
            } else {
                System.out.println("❌ Token inválido");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": true, \"mensaje\": \"Token inválido\"}");
            }
        } catch (Exception e) {
            System.out.println("❌ Error procesando JWT: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": true, \"mensaje\": \"Error procesando token\"}");
        }
    }
}