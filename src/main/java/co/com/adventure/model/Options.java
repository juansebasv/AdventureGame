package co.com.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Nodo de la historia. Cada partida es un recorrido por esta tabla: las columnas
 * {@code opt_N_value} contienen el {@code id} del siguiente nodo ({@code 0} = sin salida).
 *
 * <p>Los identificadores se asignan de forma explícita en los datos semilla, por eso no
 * hay estrategia de generación.
 */
@Entity
@Table(name = "OPTIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Options implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String description;

    @Column(name = "OPT_1_TEXT")
    private String option1Text;

    @Column(name = "OPT_2_TEXT")
    private String option2Text;

    @Column(name = "OPT_3_TEXT")
    private String option3Text;

    @Column(name = "OPT_1_VALUE")
    private int option1NextId;

    @Column(name = "OPT_2_VALUE")
    private int option2NextId;

    @Column(name = "OPT_3_VALUE")
    private int option3NextId;
}
