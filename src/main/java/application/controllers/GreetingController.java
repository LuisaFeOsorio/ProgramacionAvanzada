package application.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greeting") // Prefijo común para todas las rutas del controlador
public class GreetingController {

    @GetMapping
    public String greet(){
        return "Hola, bienvenido a la aplicación";
    }

    @GetMapping("/{name}")
    public String greetName(@PathVariable String name){
        return "Hola %s, bienvenido a la aplicación".formatted(name);
    }
}
