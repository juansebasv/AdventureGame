package co.com.adventure.exception;

/**
 * Error al entregar el SMS de notificación al proveedor externo.
 * El score ya se ha persistido cuando esta excepción se produce.
 */
public class SmsDeliveryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SmsDeliveryException(String message) {
        super(message);
    }

    public SmsDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
