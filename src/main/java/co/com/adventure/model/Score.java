package co.com.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SCORES")
public class Score implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(name = "S_HOUR")
    private int hour;

    @Column(name = "S_MINUTE")
    private int minute;

    @Column(name = "S_SECOND")
    private int second;

    @Column(name = "S_TIMESTAMP")
    private Date timestamp;
}
