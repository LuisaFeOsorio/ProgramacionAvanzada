package application.config;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    @Bean
    public Mailer mailer() {
        return MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, "pruebaprogramaciondl@gmail.com", "wzyj aeda vlmw siig")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withSessionTimeout(10 * 1000)
                .withDebugLogging(true)
                .buildMailer();
    }
}