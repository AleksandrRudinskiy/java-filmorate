package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.NotFoundException;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Getter
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        List<Film> films = filmService.getFilms();
        log.info("Accepted GET request to get a list of all movies");
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
        Film film = filmService.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильма с id = " + filmId + " нет.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(film);
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        log.info("Film " + film.getName() + " added.");
        filmService.add(film);
        return film;
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        log.info("Film is valid: " + film.getName());
        if (filmService.isAlreadyExists(film)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(filmService.update(film));
        } else if (!filmService.isAlreadyExists(film) && (film.getId() != 0)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(filmService.update(film));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(filmService.add(film));
        }
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable long filmId, @PathVariable long userId) {
        Optional<Film> film = filmService.getFilms().stream()
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
    public ResponseEntity<Film> deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        Film film = filmService.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("filmId не найден");
        }
        if (userId < 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(film);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(filmService.deleteLike(film, userId));
    }

}