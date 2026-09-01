package co.com.adventure.controller;

import co.com.adventure.dto.StatusCodeDto;
import co.com.adventure.exception.OptionNotFoundException;
import co.com.adventure.exception.SmsDeliveryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * Traduce las excepciones de la aplicación a respuestas HTTP homogéneas
 * ({@link StatusCodeDto} como cuerpo).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OptionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public StatusCodeDto handleOptionNotFound(OptionNotFoundException ex) {
        return new StatusCodeDto(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public StatusCodeDto handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new StatusCodeDto("Parámetro '" + ex.getName() + "' inválido");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public StatusCodeDto handleUnreadableBody(HttpMessageNotReadableException ex) {
        return new StatusCodeDto("Cuerpo de la petición ilegible o mal formado");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public StatusCodeDto handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::formatFieldError)
                .collect(Collectors.joining("; "));
        return new StatusCodeDto(detail);
    }

    @ExceptionHandler(SmsDeliveryException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public StatusCodeDto handleSmsDelivery(SmsDeliveryException ex) {
        return new StatusCodeDto(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public StatusCodeDto handleUnexpected(Exception ex) {
        log.error("Error no controlado", ex);
        return new StatusCodeDto("Error interno");
    }

    private static String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
