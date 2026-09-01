package co.com.adventure.dto;

import lombok.Value;

import java.io.Serializable;

/**
 * Respuesta genérica que transporta un único mensaje de estado
 * (operaciones de escritura y cuerpo de error del {@code GlobalExceptionHandler}).
 */
@Value
public class StatusCodeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    String message;
}
