package co.com.adventure.service;

import co.com.adventure.dto.ScoreDto;
import co.com.adventure.model.Score;
import co.com.adventure.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreServiceImp implements ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    @Override
    public void saveScoreByUser(ScoreDto score) throws Exception {
        Score varScore = Score.builder()
                .name(score.getName().toLowerCase())
                .hour(score.getHour())
                .minute(score.getMinute())
                .second(score.getSecond())
                .timestamp(score.getTimestamp()).build();

        scoreRepository.save(varScore);
    }

    @Override
    public List<ScoreDto> getAllScores() throws Exception {
        Iterable<Score> scoresBD = scoreRepository.findAll();
        List<ScoreDto> listScores = new ArrayList<>();
        scoresBD.forEach(var -> {
            ScoreDto varScore = ScoreDto.builder()
                    .id(var.getId())
                    .name(var.getName().substring(0, 1).toUpperCase() + var.getName().substring(1, var.getName().length()))
                    .hour(var.getHour())
                    .minute(var.getMinute())
                    .second(var.getSecond())
                    .timestamp(var.getTimestamp()).build();

            listScores.add(varScore);
        });

        return listScores;
    }

}
