package application.services.imagen;



public interface UserPhotoService {
    String cargarFoto(String userId, byte[] photoData);
    void borarFoto(String userId);
    String obtenerFoto(String userId);
}
