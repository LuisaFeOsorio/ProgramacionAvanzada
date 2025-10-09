package application.services.imagen;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImagenService {
    Map upload(MultipartFile image) throws Exception;
    Map delete(String imageId) throws Exception;
}
