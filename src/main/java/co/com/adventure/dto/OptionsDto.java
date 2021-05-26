package co.com.adventure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionsDto implements Serializable {

    private int id;
    private String description;
    private String opt_1_text;
    private String opt_2_text;
    private String opt_3_text;
    private int opt_1_value;
    private int opt_2_value;
    private int opt_3_value;
}
