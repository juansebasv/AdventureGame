package co.com.adventure.sms;

/**
 * Puerto de salida para el envío de SMS. La capa de servicio depende de esta
 * abstracción y no del proveedor concreto ni del cliente HTTP.
 *
 * <p>Implementaciones:
 * <ul>
 *   <li>{@link AltiriaSmsGateway} — activa cuando {@code adventure.sms.enabled=true}.</li>
 *   <li>{@link NoOpSmsGateway} — activa en caso contrario (no hace ninguna llamada).</li>
 * </ul>
 */
public interface SmsGateway {

    /**
     * @param nationalNumber número del destinatario sin prefijo de país
     * @param message        texto ya renderizado del mensaje
     * @throws co.com.adventure.exception.SmsDeliveryException si el proveedor no acepta el envío
     */
    void send(String nationalNumber, String message);
}
