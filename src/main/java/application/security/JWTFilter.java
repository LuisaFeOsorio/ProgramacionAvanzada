package application.security;

import application.services.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Obtener el token del header de la solicitud
        String token = getToken(request);

        // Si no hay token, continuar con la cadena de filtros
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // Validar el token y obtener el payload
            Jws<Claims> payload = jwtUtil.parseJwt(token);
            String username = payload.getBody().getSubject(); // <- usar getBody()

            // Si el usuario no está autenticado, crear un nuevo objeto de autenticación
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Cargar usuario desde UserDetailsService
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Crear un objeto de autenticación y establecerlo en el contexto de seguridad
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Si el token no es válido, enviar un error 401
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + e.getMessage());
            return;
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer "))
                ? header.substring(7)
                : null;
    }
}
