package application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {
    private boolean error;
    private String message;
    private T data;

    // Constructor simplificado para solo mensaje
    public ResponseDTO(boolean error, String message) {
        this.error = error;
        this.message = message;
        this.data = null;
    }
}