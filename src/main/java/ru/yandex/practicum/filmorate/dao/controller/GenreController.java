package ru.yandex.practicum.filmorate.dao.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.model.Genre;
import ru.yandex.practicum.filmorate.dao.service.GenreService;

import java.util.List;

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
