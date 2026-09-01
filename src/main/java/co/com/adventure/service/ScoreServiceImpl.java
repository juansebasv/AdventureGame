package co.com.adventure.service;

import co.com.adventure.dto.SaveScoreRequest;
import co.com.adventure.dto.ScoreDto;
import co.com.adventure.exception.SmsDeliveryException;
import co.com.adventure.mapper.ScoreMapper;
import co.com.adventure.model.Score;
import co.com.adventure.repository.ScoreRepository;
import co.com.adventure.sms.ScoreSmsMessageBuilder;
import co.com.adventure.sms.SmsGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {

    private final ScoreRepository repository;
    private final ScoreMapper mapper;
    private final SmsGateway smsGateway;
    private final ScoreSmsMessageBuilder messageBuilder;
    private final Clock clock;

    @Override
    @Transactional
    public void registerScore(SaveScoreRequest request) {
        Score saved = repository.save(mapper.toEntity(request, LocalDateTime.now(clock)));
        notifyPlayer(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScoreDto> findAllScores() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /** El SMS es una notificación best-effort: si falla, se registra pero no rompe la operación. */
    private void notifyPlayer(Score score) {
        try {
            smsGateway.send(score.getCellphone(), messageBuilder.build(score));
        } catch (SmsDeliveryException ex) {
            log.warn("Score {} guardado, pero falló la notificación por SMS: {}", score.getId(), ex.getMessage());
        }
    }
}
