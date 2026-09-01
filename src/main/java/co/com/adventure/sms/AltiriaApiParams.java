package co.com.adventure.sms;

/**
 * Nombres y valores del protocolo HTTP de la pasarela Altiria.
 * Solo constantes del <em>protocolo</em>; las credenciales y la URL viven en
 * {@link co.com.adventure.config.SmsProperties}.
 */
public final class AltiriaApiParams {

    public static final String COMMAND = "cmd";
    public static final String COMMAND_SEND_SMS = "sendsms";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "passwd";
    public static final String DESTINATION = "dest";
    public static final String MESSAGE = "msg";

    /** Prefijo de la respuesta cuando el proveedor rechaza la petición. */
    public static final String ERROR_RESPONSE_PREFIX = "ERROR";

    private AltiriaApiParams() {
        throw new AssertionError("No instanciable");
    }
}
