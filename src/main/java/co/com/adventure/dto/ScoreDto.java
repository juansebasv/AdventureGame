package co.com.adventure.dto;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Representación de salida de un score almacenado (respuesta de {@code GET /app/adventure/scores}).
 */
@Value
@Builder
public class ScoreDto implements Serializable {

    private static final long serialVersionUID = 1L;

    int id;

    /** Nombre del jugador, con la primera letra en mayúscula. */
    String name;

    /** Horas del tiempo empleado en completar el reto. */
    int hour;

    /** Minutos del tiempo empleado. */
    int minute;

    /** Segundos del tiempo empleado. */
    int second;

    /** Instante en que se registró la partida (zona {@code America/Bogota}). */
    LocalDateTime timestamp;

    /** Número de celular del jugador. */
    String cellphone;
}
