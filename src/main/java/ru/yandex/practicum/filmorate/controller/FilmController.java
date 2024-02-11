package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@Getter
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        log.info("Accepted GET request to get a list of all movies");
        List<Film> films = filmService.getFilms();
        log.info("Current number of films: {}", films.size());
        return films;
    }

    @GetMapping("/films/popular")
    public List<Film> findBestFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getBestFilms(count);
    }

    @GetMapping("/films/{filmId}")
    public ResponseEntity<Film> getFilmById(@PathVariable int filmId) {
        return ResponseEntity.status(HttpStatus.OK).body(filmService.getFilmById(filmId));
    }

    @PostMapping(value = "/films")
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        log.info("Film " + film.getName() + " added.");
        filmService.add(film);
        return ResponseEntity.status(HttpStatus.OK)
                .body(film);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(filmService.update(film));
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable long filmId, @PathVariable long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(filmService.addLike(filmId, userId));
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public ResponseEntity<Film> deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(filmService.deleteLike(filmId, userId));
    }
}