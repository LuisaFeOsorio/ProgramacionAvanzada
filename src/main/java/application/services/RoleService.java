package application.services;


import java.util.List;

public interface RoleService {
    List<String> getAllRoles();
    boolean roleExists(String role);
}
