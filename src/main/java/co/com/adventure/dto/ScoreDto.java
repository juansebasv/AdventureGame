package co.com.adventure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDto implements Serializable {

    private int id;
    private String name;
    private String hour;
    private String minute;
    private String second;
    private Date timestamp;
}
