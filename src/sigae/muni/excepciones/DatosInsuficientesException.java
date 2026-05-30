package sigae.muni.excepciones;

public class DatosInsuficientesException extends RuntimeException {
    public DatosInsuficientesException(String mensaje) {
        super(mensaje);
    }
}