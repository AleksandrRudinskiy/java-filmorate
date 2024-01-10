package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Getter
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        List<Film> films = filmService.getFilmStorage().getFilms();
        log.info("Accepted GET request to get a list of all movies");
        log.info("Current number of films: {}", films.size());
        return films;
    }

    @GetMapping("/films/popular")
    public List<Film> findBestFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getBestFilms().stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @GetMapping("/films/{filmId}")
    public ResponseEntity<Optional<Film>> getFilmById(@PathVariable int filmId) {
        Optional<Film> optionalFilm = filmService.getFilmStorage().getFilms().stream()
                .filter(item -> item.getId() == filmId)
                .findFirst();
        if (optionalFilm.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(optionalFilm);
        }
        return ResponseEntity.status(HttpStatus.OK).body(optionalFilm);
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        log.info("Film " + film.getName() + " added.");
        filmService.getFilmStorage().add(film);
        return film;
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        log.info("Film is valid: " + film.getName());
        if (filmService.getFilmStorage().isAlreadyExists(film)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(filmService.getFilmStorage().update(film));
        } else if (!filmService.getFilmStorage().isAlreadyExists(film) && (film.getId() != 0)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(filmService.getFilmStorage().update(film));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(filmService.getFilmStorage().add(film));
        }
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable long filmId, @PathVariable long userId) {
        Optional<Film> film = filmService.getFilmStorage().getFilms().stream()
                .filter(item -> item.getId() == filmId)
                .findFirst();
        if (film.isPresent()) {
            filmService.addLike(film.get(), userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(filmService.addLike(film.get(), userId));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film.get());
        }
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public ResponseEntity<Optional<Film>> deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        Optional<Film> film = filmService.getFilmStorage().getFilms().stream()
                .filter(item -> item.getId() == filmId)
                .findFirst();
        if (userId < 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(film);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Optional.of(filmService.deleteLike(film.get(), userId)));
    }

}