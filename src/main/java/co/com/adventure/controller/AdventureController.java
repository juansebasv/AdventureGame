package co.com.adventure.controller;

import co.com.adventure.dto.OptionsDto;
import co.com.adventure.dto.SaveScoreRequest;
import co.com.adventure.dto.ScoreDto;
import co.com.adventure.dto.StatusCodeDto;
import co.com.adventure.exception.OptionNotFoundException;
import co.com.adventure.service.OptionsService;
import co.com.adventure.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * API del juego de aventura.
 *
 * <p>El manejo de errores está centralizado en {@link GlobalExceptionHandler};
 * por eso los métodos no contienen bloques {@code try/catch}.
 */
@Tag(name = "Adventure", description = "Nodos de la historia y scores de las partidas")
@RestController
@RequestMapping(AdventureRoutes.BASE)
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class AdventureController {

    private static final String SCORE_SAVED_MESSAGE = "saved";

    private final OptionsService optionsService;
    private final ScoreService scoreService;

    @Operation(summary = "Obtener un nodo de la historia",
            description = "Devuelve el nodo con el id indicado: su texto narrativo y hasta 3 opciones. "
                    + "En cada opción, `opt_N_value` es el id del siguiente nodo (0 = la opción no existe / es un final).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nodo encontrado"),
            @ApiResponse(responseCode = "400", description = "El id no es un número válido",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un nodo con ese id",
                    content = @Content)})
    @PostMapping(AdventureRoutes.OPTION_BY_ID)
    public OptionsDto getOption(
            @Parameter(description = "Id del nodo a recuperar", example = "1")
            @PathVariable int id) {
        return optionsService.findOption(id)
                .orElseThrow(() -> new OptionNotFoundException(id));
    }

    @Operation(summary = "Guardar el score de una partida",
            description = "Persiste el tiempo empleado por el jugador e intenta notificarle por SMS. "
                    + "El fallo del SMS no revierte el guardado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Score guardado"),
            @ApiResponse(responseCode = "400", description = "Cuerpo de la petición inválido",
                    content = @Content)})
    @PostMapping(AdventureRoutes.SAVE_SCORE)
    @ResponseStatus(HttpStatus.CREATED)
    public StatusCodeDto saveScore(@Valid @RequestBody SaveScoreRequest request) {
        scoreService.registerScore(request);
        return new StatusCodeDto(SCORE_SAVED_MESSAGE);
    }

    @Operation(summary = "Listar todos los scores",
            description = "Devuelve todos los scores almacenados, con el nombre capitalizado.")
    @ApiResponse(responseCode = "200", description = "Listado de scores")
    @GetMapping(AdventureRoutes.SCORES)
    public List<ScoreDto> getScores() {
        return scoreService.findAllScores();
    }
}
