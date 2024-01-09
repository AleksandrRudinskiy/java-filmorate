package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.servlet.http.HttpServletResponse;
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
    public Optional<Film> getFilmById(@PathVariable int filmId, HttpServletResponse response) {
        Optional<Film> optionalFilm = filmService.getFilmStorage().getFilms().stream()
                .filter(item -> item.getId() == filmId)
                .findFirst();
        if (optionalFilm.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return optionalFilm;
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        log.info("Film " + film.getName() + " added.");
        filmService.getFilmStorage().add(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film, HttpServletResponse response) {
        log.info("Film is valid: " + film.getName());
        if (filmService.getFilmStorage().isAlreadyExists(film)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return filmService.getFilmStorage().update(film);
        } else if (!filmService.getFilmStorage().isAlreadyExists(film) && (film.getId() != 0)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return film;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return filmService.getFilmStorage().add(film);
        }
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public Film addLike(@PathVariable long filmId, @PathVariable long userId, HttpServletResponse response) {
        Optional<Film> film = filmService.getFilmStorage().getFilms().stream()
                .filter(item -> item.getId() == filmId)
                .findFirst();

        if (film.isPresent()) {
            filmService.addLike(film.get(), userId);
            return filmService.addLike(film.get(), userId);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return film.get();
        }
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable long filmId, @PathVariable long userId, HttpServletResponse response) {
        Optional<Film> film = filmService.getFilmStorage().getFilms().stream()
                .filter(item -> item.getId() == filmId)
                .findFirst();
        if (userId < 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return filmService.deleteLike(film.get(), userId);
    }

}