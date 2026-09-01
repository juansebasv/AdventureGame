package co.com.adventure.exception;

/**
 * Se lanza cuando se solicita un nodo de la historia que no existe.
 * El {@code GlobalExceptionHandler} la traduce a un HTTP 404.
 */
public class OptionNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OptionNotFoundException(int optionId) {
        super("No existe ninguna opción con id " + optionId);
    }
}
