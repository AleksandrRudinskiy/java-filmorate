package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        log.info("GET-Запрос на получение всех фильмов.");
        List<Film> films = filmService.getFilms();
        log.info("Количество фильмов: {}.", films.size());
        return films;
    }

    @GetMapping("/films/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilmsByDirectorSorted(
            @PathVariable long directorId,
            @RequestParam(required = false, defaultValue = "likes") String sortBy) {
        return filmService.findAllByDirectorIdSorted(directorId, sortBy);
    }

    @GetMapping("/films/popular")
    public List<Film> getBestFilms(
            @RequestParam(defaultValue = "0", required = false) int genreId,
            @RequestParam(defaultValue = "0", required = false) int year,
            @RequestParam(defaultValue = "10", required = false) int count) {
        log.info("GET-Запрос на получение топ-списка {} фильмов.", count);
        return filmService.getBestFilms(genreId, year, count);
    }

    @GetMapping("/films/{filmId}")
    public ResponseEntity<Film> getFilmById(@PathVariable int filmId) {
        log.info("GET-Запрос на получение фильма по id = {}.", filmId);
        return ResponseEntity.status(HttpStatus.OK).body(filmService.getFilmById(filmId));
    }

    @GetMapping(value = "/films/common", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("GET-Запрос на получение общих фильмов двух пользователей.");
        return filmService.getCommonFilms(userId, friendId);
    }

    @PostMapping(value = "/films")
    public ResponseEntity<Film> createFilm(@RequestBody Film film) {
        log.info("POST-запрос на добавление фильма.");
        filmService.add(film);
        log.info("Фильм с названием {} добавлен.", film.getName());
        return ResponseEntity.status(HttpStatus.OK)
                .body(film);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        log.info("PUT-запрос на обновление фильма с id = {}.", film.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(filmService.update(film));
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("PUT-Запрос за лайк фильму с filmId = {} от пользователя с userId = {}.", filmId, userId);
        filmService.addLike(filmId, userId);
        ResponseEntity.status(HttpStatus.OK);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public ResponseEntity<Film> deleteLike(@PathVariable long filmId, @PathVariable long userId) {
        log.info("DELETE-Запрос на удаление лайка фильма с filmId = {} от пользователя с userId = {}.", filmId, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(filmService.deleteLike(filmId, userId));
    }

    @DeleteMapping("/films/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFilm(@PathVariable long filmId) {
        log.info("DELETE-Запрос на удаление фильма и всех связанных с ним данных.");
        filmService.deleteFilm(filmId);
    }

    @GetMapping(value = "/films/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<Film> search(@RequestParam String query, @RequestParam String by) {
        log.info("GET-Запрос на поиск фильма. Query = {}, by = {}", query, by);
        return filmService.search(query, by);
    }
}