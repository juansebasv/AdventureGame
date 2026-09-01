package co.com.adventure.service;

import co.com.adventure.dto.SaveScoreRequest;
import co.com.adventure.dto.ScoreDto;

import java.util.List;

/** Casos de uso relacionados con los tiempos (scores) de las partidas. */
public interface ScoreService {

    /**
     * Persiste el resultado de una partida e intenta notificar al jugador por SMS.
     * El fallo de la notificación no revierte el guardado.
     */
    void registerScore(SaveScoreRequest request);

    /** Devuelve todos los scores almacenados. */
    List<ScoreDto> findAllScores();
}
