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
    private String hour;

    @Column(name = "S_MINUTE")
    private String minute;

    @Column(name = "S_SECOND")
    private String second;

    @Column(name = "S_TIMESTAMP")
    private Date timestamp;
}
