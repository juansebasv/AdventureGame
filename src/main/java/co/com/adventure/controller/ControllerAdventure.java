package co.com.adventure.controller;

import co.com.adventure.dto.OptionsDto;
import co.com.adventure.dto.ScoreDto;
import co.com.adventure.dto.StatusCodeDto;
import co.com.adventure.service.OptionsService;
import co.com.adventure.service.ScoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class ControllerAdventure {

    @Autowired
    private OptionsService service;
    @Autowired
    private ScoreService scoreService;

    @ApiOperation(value = "Validar valores de las opciones digitadas",
            notes = "Se validan los valores para retornar la siguiente opción",
            response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = OptionsDto.class, message = "OK"),
            @ApiResponse(code = 404, response = OptionsDto.class, message = "Not found")})
    @PostMapping("/adventure/{valor}")
    public ResponseEntity<OptionsDto> validateOptions(@PathVariable("valor") String valor) {
        try {
            if (valor != null && valor.trim().length() != 0 && !valor.equals("0")) {
                int option = Integer.parseInt(valor);
                OptionsDto optionsDto = service.validateOption(option);
                if (null == optionsDto) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                } else {
                    return new ResponseEntity<>(optionsDto, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Guardar el score de cada uno de los usuarios",
            notes = "Se guardan todos los scores de los usuarios en una tabla general",
            response = StatusCodeDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, response = OptionsDto.class, message = "Saved"),
            @ApiResponse(code = 404, response = OptionsDto.class, message = "Not found"),
            @ApiResponse(code = 406, response = OptionsDto.class, message = "Not Acceptable")})
    @PostMapping("/adventure/saveScore")
    public ResponseEntity<StatusCodeDto> saveScoreByUser(@RequestBody(required = true) ScoreDto score) {
        StatusCodeDto code = new StatusCodeDto();
        try {
            if (null != score) {
                scoreService.saveScoreByUser(score);
                code.setMessage("saved");
                return new ResponseEntity<>(code, HttpStatus.CREATED);
            } else {
                code.setMessage("error");
                return new ResponseEntity<>(code, HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            code.setMessage(ex.getMessage());
            return new ResponseEntity<>(code, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Consultar todos los registros de la tabla",
            notes = "Consulta todos los scores almacenados de los diferentes usuarios",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = OptionsDto.class, message = "OK"),
            @ApiResponse(code = 404, response = OptionsDto.class, message = "Not found"),
            @ApiResponse(code = 406, response = OptionsDto.class, message = "Not Acceptable")})
    @GetMapping("/adventure/scores")
    public ResponseEntity<List<ScoreDto>> getAllScores() {
        try {
            List<ScoreDto> listScores = scoreService.getAllScores();
            if (null != listScores) {
                return new ResponseEntity<>(listScores, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
