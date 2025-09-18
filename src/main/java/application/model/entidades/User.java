package application.model.entidades;

import application.model.Role;
import application.model.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class User {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String photoUrl;
    private LocalDate dateBirth;
    private LocalDateTime createdAt;
    private Role role;
    private UserStatus status;
}

