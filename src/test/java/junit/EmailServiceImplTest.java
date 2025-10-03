package junit;

import application.Application;
import application.dto.email.EmailDTO;
import application.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = Application.class)
class EmailServiceImplTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testEnviarCorreo() throws Exception {
        EmailDTO emailDTO = new EmailDTO(
                "pruebaprogramaciondl@gmail.com",
                "Prueba de JUnit",
                "Este es un correo de prueba con Spring Boot y JUnit."
        );

        emailService.sendMail(emailDTO);

    }
}

