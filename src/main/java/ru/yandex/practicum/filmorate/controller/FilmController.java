package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.ValidationException;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@Getter
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final LocalDate startReleaseDate = LocalDate.of(1895, 12, 28);

    @GetMapping("/films")
    public List<Film> findAllFilms() {

        List<Film> films = filmService.getFilms();
        log.info("Accepted GET request to get a list of all movies");
        log.info("Current number of films: {}", films.size());
        return films;
    }

    @PostMapping(value = "/films")
    public Film createFilm(@RequestBody Film film) {
        validateFilm(film);
        log.info("Film " + film.getName() + " added");
        filmService.add(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film, HttpServletResponse response) {
        validateFilm(film);
        log.info("Film is valid :" + film.getName());
        if (filmService.isAlreadyExists(film)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return filmService.update(film);
        } else if (!filmService.isAlreadyExists(film) && (film.getId() != 0)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return film;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return filmService.add(film);
        }

    }

    @ExceptionHandler({ValidationException.class})
    private void validateFilm(Film film) {
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ValidationException("The name is empty");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("The description length more than 200 symbols");
        }
        if (film.getReleaseDate().isBefore(startReleaseDate)) {
            throw new ValidationException("Release date earlier than 28-12-1895");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Film duration is negative");
        }
    }
}
