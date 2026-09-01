package co.com.adventure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * Representación de salida de un nodo de la historia.
 *
 * <p>Los nombres JSON ({@code opt_1_text}, {@code opt_1_value}, ...) se conservan mediante
 * {@link JsonProperty} para no romper el contrato con los clientes existentes, mientras que
 * los atributos Java siguen la convención camelCase y nombres con significado.
 */
@Value
@Builder
public class OptionsDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Id del nodo actual. */
    int id;

    /** Texto narrativo que se muestra al jugador. */
    String description;

    @JsonProperty("opt_1_text")
    String option1Text;

    @JsonProperty("opt_2_text")
    String option2Text;

    @JsonProperty("opt_3_text")
    String option3Text;

    /** Id del nodo al que lleva la opción 1 ({@code 0} = la opción no existe / final). */
    @JsonProperty("opt_1_value")
    int option1NextId;

    @JsonProperty("opt_2_value")
    int option2NextId;

    @JsonProperty("opt_3_value")
    int option3NextId;
}
