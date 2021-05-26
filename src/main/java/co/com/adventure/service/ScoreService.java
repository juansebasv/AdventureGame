package co.com.adventure.service;

import co.com.adventure.dto.ScoreDto;

import java.util.List;

public interface ScoreService {

    public void saveScoreByUser(ScoreDto score) throws Exception;

    public List<ScoreDto> getAllScores() throws Exception;

}
