package application.dto;



import application.model.Role;

import java.time.LocalDate;

public class UserDTO {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String photoUrl;
    private LocalDate dateBirth;
    private Role role;

    // Constructor vacío
    public UserDTO() {}

    // Constructor con parámetros
    public UserDTO(String id, String name, String phone, String email, String photoUrl, LocalDate dateBirth, Role role) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.photoUrl = photoUrl;
        this.dateBirth = dateBirth;
        this.role = role;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDate getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(LocalDate dateBirth) {
        this.dateBirth = dateBirth;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

