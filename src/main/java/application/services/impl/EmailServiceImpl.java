package application.services.impl;

import application.dto.email.EmailDTO;
import application.services.EmailService;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static org.simplejavamail.config.ConfigLoader.Property.SMTP_PORT;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    @Async
    public void sendMail(EmailDTO emailDTO) throws Exception {
        Email email = EmailBuilder.startingBlank()
                .from("pruebaprogramaciondl@gmail.com")
                .to(emailDTO.recipient())
                .withSubject(emailDTO.subject())
                .withPlainText(emailDTO.body())
                .buildEmail();

        try (Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "pruebaprogramaciondl@gmail.com", "wzyj aeda vlmw siig")

                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withDebugLogging(true)
                .buildMailer()) {

            mailer.sendMail(email);
        }
    }

}
