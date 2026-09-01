package co.com.adventure.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Gateway de SMS inactivo. Se registra cuando {@code adventure.sms.enabled} es
 * {@code false} o no está definido, de modo que en local el score se guarda sin
 * intentar ninguna conexión externa.
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "adventure.sms", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoOpSmsGateway implements SmsGateway {

    @Override
    public void send(String nationalNumber, String message) {
        log.debug("Envío de SMS deshabilitado; se omite el mensaje al número {}", nationalNumber);
    }
}
