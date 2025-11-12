package application.model;

import application.model.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String nombre;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String contrasenia;

    @Column(length = 50)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    
    private Role rol;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "foto_perfil", length = 1000)
    private String foto_perfil;

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    // CAMPOS PARA ANFITRIONES
    @Column(name = "descripcion_personal", length = 500)
    private String descripcionPersonal;

    @Column(name = "documento_identidad", length = 100)
    private String documentoIdentidad;

    @Column(name = "archivo_documentos", length = 200)
    private String archivoDocumentos;

    @Column(name = "documentos_verificados")
    private Boolean documentosVerificados = Boolean.FALSE;

    @OneToMany(mappedBy = "anfitrion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alojamiento> alojamientos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();



    // Constructor para creación básica
    public Usuario(String nombre, String email, String contrasenia, Role rol) {
        this.nombre = nombre;
        this.email = email;
        this.contrasenia = contrasenia;
        this.rol = rol;
        this.activo = true;
        this.documentosVerificados = false;
    }

    public Usuario() {

    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public String getTelefono() {
        return telefono;
    }

    public Role getRol() {
        return rol;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getFoto_perfil() {
        return foto_perfil;
    }

    public Boolean getActivo() {
        return activo;
    }

    public String getDescripcionPersonal() {
        return descripcionPersonal;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public String getArchivoDocumentos() {
        return archivoDocumentos;
    }

    public Boolean getDocumentosVerificados() {
        return documentosVerificados;
    }

    public List<Alojamiento> getAlojamientos() {
        return alojamientos;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }


}