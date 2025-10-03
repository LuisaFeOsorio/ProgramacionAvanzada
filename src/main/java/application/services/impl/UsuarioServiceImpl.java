package application.services.impl;

import application.dto.contraseña.CambioContraseniaDTO;
import application.dto.contraseña.ResetContraseniaDTO;
import application.dto.usuario.*;
import application.exceptions.NotFoundException;
import application.exceptions.ValidationException;
import application.exceptions.ValueConflictException;
import application.mappers.UsuarioMapping;
import application.model.entidades.Usuario;
import application.model.enums.UserStatus;
import application.repositories.UsuarioRepository;
import application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UserService {

    @Override
    public void create(CrearUsuarioDTO usuarioDTO) throws Exception {

    }

    @Override
    public UsuarioDTO get(String id) throws Exception {
        return null;
    }

    @Override
    public void delete(String id) throws Exception {

    }

    @Override
    public List<UsuarioDTO> listAll() {
        return List.of();
    }

    @Override
    public void edit(String id, EditarUsuarioDTO userDTO) throws Exception {

    }
}
