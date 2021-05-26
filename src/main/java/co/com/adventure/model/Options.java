package co.com.adventure.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OPTIONS")
public class Options implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @Column(name = "OPT_1_TEXT")
    private String opt_1_text;

    @Column(name = "OPT_2_TEXT")
    private String opt_2_text;

    @Column(name = "OPT_3_TEXT")
    private String opt_3_text;

    @Column(name = "OPT_1_VALUE")
    private int opt_1_value;

    @Column(name = "OPT_2_VALUE")
    private int opt_2_value;

    @Column(name = "OPT_3_VALUE")
    private int opt_3_value;

}
