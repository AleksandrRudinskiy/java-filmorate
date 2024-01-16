package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@Getter
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LocalDate startReleaseDate = LocalDate.of(1895, 12, 28);

    public Film addLike(long id, long userId) {
        if (!filmStorage.isAlreadyExists(id)) {
            throw new NotFoundException("film с id = " + id + " не найден");
        }
        Film film = filmStorage.getFilmById(id);
        film.getLikes().add(userId);
        return film;
    }

    public Film deleteLike(long id, long userId) {
        if (!filmStorage.isAlreadyExists(id)) {
            throw new NotFoundException("film с id = " + id + " не найден");
        }
        if (userId < 0) {
            throw new NotFoundException("Неверный id пользователя");
        }
        Film film = getFilmById(id);
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getBestFilms(int count) {
        return filmStorage.getBestFilms(count);
    }

    public List<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public void add(Film film) {
        validateFilm(film);
        filmStorage.add(film);
    }

    public Film update(Film film) {
        if (filmStorage.isAlreadyExists(film.getId())) {
            return filmStorage.update(film);
        } else if (!filmStorage.isAlreadyExists(film.getId()) && (film.getId() != 0)) {
            throw new RuntimeException();
        } else {
            return filmStorage.add(film);
        }
    }

    public Film getFilmById(long id) {
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
