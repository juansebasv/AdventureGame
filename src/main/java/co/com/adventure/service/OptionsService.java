package co.com.adventure.service;

import co.com.adventure.dto.OptionsDto;

import java.util.Optional;

/** Casos de uso relacionados con los nodos de la historia. */
public interface OptionsService {

    /**
     * Devuelve el nodo con el id indicado.
     *
     * @return el nodo, o {@link Optional#empty()} si no existe
     */
    Optional<OptionsDto> findOption(int id);
}
