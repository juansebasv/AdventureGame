package co.com.adventure.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * Cuerpo de la petición {@code POST /app/adventure/saveScore}.
 *
 * <p>Se valida con Bean Validation ({@code @Valid} en el controlador). Los campos que
 * el modelo de dominio calcula por su cuenta ({@code id}, {@code timestamp}) ya no forman
 * parte del contrato de entrada.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class SaveScoreRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int MIN_TIME_UNIT = 0;
    private static final int MAX_MINUTE_OR_SECOND = 59;
    private static final String CELLPHONE_PATTERN = "\\d{7,15}";

    @NotBlank
    private String name;

    @Min(MIN_TIME_UNIT)
    private int hour;

    @Min(MIN_TIME_UNIT)
    @Max(MAX_MINUTE_OR_SECOND)
    private int minute;

    @Min(MIN_TIME_UNIT)
    @Max(MAX_MINUTE_OR_SECOND)
    private int second;

    @NotBlank
    @Pattern(regexp = CELLPHONE_PATTERN, message = "cellphone must contain between 7 and 15 digits")
    private String cellphone;
}
