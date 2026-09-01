package co.com.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Resultado de una partida: nombre del jugador, tiempo empleado (h/m/s), instante
 * de registro y celular al que se envía la notificación por SMS.
 */
@Entity
@Table(name = "SCORES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Score implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "S_HOUR")
    private int hour;

    @Column(name = "S_MINUTE")
    private int minute;

    @Column(name = "S_SECOND")
    private int second;

    @Column(name = "S_TIMESTAMP")
    private LocalDateTime timestamp;

    @Column(name = "CELLPHONE")
    private String cellphone;
}
