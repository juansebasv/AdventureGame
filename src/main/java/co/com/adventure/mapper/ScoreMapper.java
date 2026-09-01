package co.com.adventure.mapper;

import co.com.adventure.dto.SaveScoreRequest;
import co.com.adventure.dto.ScoreDto;
import co.com.adventure.model.Score;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Conversión entre la petición de entrada, la entidad {@link Score} y el DTO de salida.
 *
 * <p>Normaliza el nombre del jugador: se almacena en minúsculas y se expone con la
 * primera letra en mayúscula.
 */
@Component
public class ScoreMapper {

    public Score toEntity(SaveScoreRequest request, LocalDateTime registeredAt) {
        return Score.builder()
                .name(request.getName().toLowerCase(Locale.ROOT))
                .hour(request.getHour())
                .minute(request.getMinute())
                .second(request.getSecond())
                .timestamp(registeredAt)
                .cellphone(request.getCellphone())
                .build();
    }

    public ScoreDto toDto(Score entity) {
        return ScoreDto.builder()
                .id(entity.getId())
                .name(StringUtils.capitalize(entity.getName()))
                .hour(entity.getHour())
                .minute(entity.getMinute())
                .second(entity.getSecond())
                .timestamp(entity.getTimestamp())
                .cellphone(entity.getCellphone())
                .build();
    }
}
