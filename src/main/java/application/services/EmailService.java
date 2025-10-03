package application.services;

import application.dto.email.EmailDTO;

public interface EmailService {
    void sendMail(EmailDTO emailDTO) throws Exception;
}
