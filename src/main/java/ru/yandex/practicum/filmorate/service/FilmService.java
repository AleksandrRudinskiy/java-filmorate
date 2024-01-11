package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Getter
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    private final LocalDate startReleaseDate = LocalDate.of(1895, 12, 28);

    public Film addLike(Film film, long userId) {
        return filmStorage.addLike(film, userId);
    }

    public Film deleteLike(Film film, long userId) {
        return filmStorage.deleteLike(film, userId);
    }

    public List<Film> getBestFilms(int count) {
        return filmStorage.getBestFilms(count);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film add(Film film) {
        validateFilm(film);
        return filmStorage.add(film);
    }

    public boolean isAlreadyExists(Film film) {
        return filmStorage.isAlreadyExists(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

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
