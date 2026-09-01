package co.com.adventure.service;

import co.com.adventure.dto.OptionsDto;
import co.com.adventure.mapper.OptionMapper;
import co.com.adventure.repository.OptionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OptionsServiceImpl implements OptionsService {

    private final OptionsRepository repository;
    private final OptionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<OptionsDto> findOption(int id) {
        return repository.findById(id).map(mapper::toDto);
    }
}
