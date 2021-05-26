package co.com.adventure.service;

import co.com.adventure.dto.OptionsDto;
import co.com.adventure.model.Options;
import co.com.adventure.repository.OptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OptionsServiceImp implements OptionsService {

    @Autowired
    private OptionsRepository repository;

    @Override
    public OptionsDto validateOption(int value) {
        Optional<Options> result = repository.findById(value);
        OptionsDto option = new OptionsDto();

        if (!result.isPresent()) {
            return null;
        }

        option = OptionsDto.builder().id(result.get().getId())
                .description(result.get().getDescription())
                .opt_1_text(result.get().getOpt_1_text())
                .opt_2_text(result.get().getOpt_2_text())
                .opt_3_text(result.get().getOpt_3_text())
                .opt_1_value(result.get().getOpt_1_value())
                .opt_2_value(result.get().getOpt_2_value())
                .opt_3_value(result.get().getOpt_3_value()).build();

        return option;
    }
}
