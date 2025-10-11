package application.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@Order(1)
public class DebugFiltrer implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (httpRequest.getServletPath().equals("/api/usuarios") &&
                "POST".equalsIgnoreCase(httpRequest.getMethod())) {

            System.out.println("🚨 === DEBUG FILTER: POST /api/usuarios DETECTADO ===");
            System.out.println("🚨 Content-Type: " + httpRequest.getContentType());
            System.out.println("🚨 Headers: " + Collections.list(httpRequest.getHeaderNames()));
        }

        chain.doFilter(request, response);
    }
}