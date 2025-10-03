package junit;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CloudinaryTest {

    @Test
    void testUploadImage() throws Exception {
        // 1. Configurar cloudinary con tus credenciales
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dzjtafatn",
                "api_key", "255329149496492",
                "api_secret", "xXBlXsId3ELyxBzFYGBOQDTu-78"
        ));

        File file = new File("src/test/resources/imagenes/images.jpg");
        assertTrue(file.exists(), "La imagen de prueba no existe");

        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());


        assertNotNull(uploadResult.get("url"));
        System.out.println("✅ Imagen subida correctamente: " + uploadResult.get("url"));

//        String publicId = (String) uploadResult.get("public_id");
//        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//        System.out.println("🗑 Imagen borrada con public_id: " + publicId);
    }
}

