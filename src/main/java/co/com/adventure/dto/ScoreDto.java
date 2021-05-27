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
    private int hour;
    private int minute;
    private int second;
    private Date timestamp;
}
