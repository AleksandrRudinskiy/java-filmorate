package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.util.Map;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidationException(final ValidationException e) {
        return new ResponseEntity<>(
                Map.of("error", "Ошибка валидации.",
                        "errorMessage", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

}
