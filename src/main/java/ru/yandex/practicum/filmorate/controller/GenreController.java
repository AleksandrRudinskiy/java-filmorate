package ru.yandex.practicum.filmorate.controller;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.*;

import java.util.*;

@RestController
@Slf4j
@AllArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        log.info("GET-запрос на получение всех жанров.");
        List<Genre> genres = genreService.getGenres();
        log.info("Количество жанров: {}.", genres.size());
        return genres;
    }

    @GetMapping("/genres/{genreId}")
    public ResponseEntity<Genre> getUserById(@PathVariable int genreId) {
        log.info("GET-запрос на получение жанра по id = {}.", genreId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(genreService.getGenreById(genreId));
    }
}
