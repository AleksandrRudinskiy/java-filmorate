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
        log.info("Запрос на получение всех фильмов.");
        List<Film> films = filmService.getFilms();
        log.info("Количество фильмов: {}.", films.size());
        return films;
    }

    @GetMapping("/films/popular")
    public List<Film> findBestFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("Запрос на получение топ-списка {} фильмов.", count);
        return filmService.getBestFilms(count);
    }

    @GetMapping("/films/{filmId}")
    public ResponseEntity<Film> getFilmById(@PathVariable int filmId) {
        log.info("Запрос на получение фильма по id = {}.", filmId);
        return ResponseEntity.status(HttpStatus.OK).body(filmService.getFilmById(filmId));
    }

    @PostMapping(value = "/films")
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        log.info("Запрос на добавление фильма.");
        filmService.add(film);
        log.info("Фильм с названием {} добавлен.", film.getName());
        return ResponseEntity.status(HttpStatus.OK)
                .body(film);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        log.info("Запрос на обновление фильма с id = {}.", film.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(filmService.update(film));
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Запрос за лайк фильму с filmId = {} от пользователя с userId = {}.", filmId, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(filmService.addLike(filmId, userId));
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public ResponseEntity<Film> deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("Запрос на удаление лайка фильма с filmId = {} от пользователя с userId = {}.", filmId, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(filmService.deleteLike(filmId, userId));
    }
}