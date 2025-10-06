package application.exceptions.usuario;

public class EmailEnUsoException extends Exception{
    public EmailEnUsoException(String message) {
        super(message);
    }
}
