package application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    //*Esta clase habilita y configura la funcionalidad asíncrona en Spring.
    // Está vacía porque usaremos la configuración por defecto. Para usar la
    // funcionalidad asíncrona,
    // podemos anotar métodos con @Async en cualquier bean de Spring.
}