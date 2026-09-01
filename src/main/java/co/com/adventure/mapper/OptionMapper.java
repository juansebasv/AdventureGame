package co.com.adventure.mapper;

import co.com.adventure.dto.OptionsDto;
import co.com.adventure.model.Options;
import org.springframework.stereotype.Component;

/**
 * Convierte entidades {@link Options} en su representación de salida {@link OptionsDto}.
 * Aísla a la capa de servicio del detalle de construcción del DTO.
 */
@Component
public class OptionMapper {

    public OptionsDto toDto(Options entity) {
        return OptionsDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .option1Text(entity.getOption1Text())
                .option2Text(entity.getOption2Text())
                .option3Text(entity.getOption3Text())
                .option1NextId(entity.getOption1NextId())
                .option2NextId(entity.getOption2NextId())
                .option3NextId(entity.getOption3NextId())
                .build();
    }
}
