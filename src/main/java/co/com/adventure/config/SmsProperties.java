package co.com.adventure.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

/**
 * Configuración de la pasarela de SMS, bindeada desde el prefijo {@code adventure.sms}
 * de {@code application.yml} (o variables de entorno equivalentes).
 *
 * <p>Sustituye a las credenciales y URLs que antes estaban embebidas en el código
 * fuente ({@code util.Constants}).
 */
@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "adventure.sms")
public class SmsProperties {

    /** Si es {@code false}, no se realiza ninguna llamada externa (se usa un gateway no-op). */
    private final boolean enabled;

    /** URL del endpoint HTTP del proveedor de SMS. */
    private final String url;

    /** Usuario del proveedor de SMS. */
    private final String login;

    /** Contraseña del proveedor de SMS. */
    private final String password;

    /** Prefijo telefónico de país que se antepone al número nacional del destinatario. */
    private final String senderCountryCode;

    /** Timeout de establecimiento de conexión con el proveedor. */
    private final Duration connectTimeout;

    /** Timeout de lectura de la respuesta del proveedor. */
    private final Duration socketTimeout;

    /**
     * Plantilla del mensaje. Marcadores admitidos: {@code {name}}, {@code {score}}, {@code {date}}.
     */
    private final String messageTemplate;
}
