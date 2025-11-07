package application.config;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MailConfig {

    @Value("${spring.mail.username:pruebaprogramaciondl@gmail.com}")
    private String username;

    @Value("${spring.mail.password:lrap tzji ctyf swvh}")
    private String password;

    @Bean
    public Mailer mailer() {
        return MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, username, password)
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .withProperty("mail.smtp.auth", "true")
                .withProperty("mail.smtp.starttls.enable", "true")
                .withProperty("mail.smtp.starttls.required", "true")
                .withProperty("mail.smtp.ssl.trust", "smtp.gmail.com")
                .withSessionTimeout(10 * 1000)
                .buildMailer();
    }
}