package application.security;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        System.out.println(" === JWT FILTER START ===");
        System.out.println("Path: " + request.getServletPath());
        System.out.println("Method: " + request.getMethod());

        System.out.println("=== HEADERS ===");
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                System.out.println(headerName + ": " + request.getHeader(headerName))
        );
        System.out.println("===============");

        // Obtener el token del header de la solicitud
        String token = getToken(request);

        if (token == null) {
            System.out.println(" NO TOKEN FOUND - Headers above should show Authorization");
            chain.doFilter(request, response);
            return;
        }

        try {
            System.out.println(" Token found, validating...");

            // Validar el token y obtener el payload
            Jws<Claims> payload = jwtUtil.parseJwt(token);
            String username = payload.getBody().getSubject();

            System.out.println(" Token validated for user: " + username);

            // Si el usuario no está autenticado, crear un nuevo objeto de autenticación
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println(" Loading user details for: " + username);

                // Cargar usuario desde UserDetailsService
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                System.out.println(" User details loaded: " + userDetails.getUsername());
                System.out.println("Authorities: " + userDetails.getAuthorities());

                // Crear objeto de autenticación y establecerlo en el contexto
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println(" SecurityContext set for user: " + username);
            }

        } catch (Exception e) {
            System.out.println(" JWT validation failed: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + e.getMessage());
            return;
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        System.out.println(" Raw Authorization Header: " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = cleanToken(header.substring(7));
            System.out.println(" Cleaned token: " + token.substring(0, Math.min(20, token.length())) + "...");
            System.out.println("Token length: " + token.length());
            return token;
        } else {
            System.out.println(" No Bearer token found");
        }
        return null;
    }

    private String cleanToken(String token) {
        if (token == null) return null;

        token = token.trim();
        if ((token.startsWith("\"") && token.endsWith("\"")) ||
                (token.startsWith("'") && token.endsWith("'"))) {
            token = token.substring(1, token.length() - 1);
        }

        return token.trim();
    }
}
