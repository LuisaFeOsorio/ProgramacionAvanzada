package application.services;



public interface UserPhotoService {
    String uploadPhoto(String userId, byte[] photoData);
    void deletePhoto(String userId);
    String getPhotoUrl(String userId);
}
