package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@Data
@AllArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        log.info("Accepted GET request to get a list of films genres");
        List<Genre> genres = genreService.getGenres();
        log.debug("Current number of genres: {}", genres.size());
        return genres;
    }

    @GetMapping("/genres/{genreId}")
    public ResponseEntity<Genre> getUserById(@PathVariable int genreId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(genreService.getGenreById(genreId));
    }
}
