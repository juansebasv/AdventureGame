package co.com.adventure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Expone un {@link Clock} con la zona horaria de negocio de la aplicación.
 *
 * <p>Inyectar {@code Clock} en lugar de llamar a {@code LocalDateTime.now()} directamente
 * mantiene la lógica temporal testeable (se puede fijar la hora en los tests).
 */
@Configuration
public class TimeConfig {

    /** Zona horaria en la que se registran los tiempos de las partidas. */
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Bogota");

    @Bean
    public Clock clock() {
        return Clock.system(DEFAULT_ZONE);
    }
}
