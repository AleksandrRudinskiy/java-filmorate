package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LocalDate startReleaseDate = LocalDate.of(1895, 12, 28);

    public void addLike(long id, long userId) {
        filmStorage.addLike(id, userId);
    }

    /**
     * Удаляет лайк пользователя к фильму.
     *
     * @param id     идентификатор фильма, для которого нужно удалить лайк.
     * @param userId идентификатор пользователя, чей лайк нужно удалить.
     * @return Film  возвращает объект фильма, для которого был удален лайк.
     * @throws NotFoundException если фильм с указанным идентификатором не найден.
     */
    public Film deleteLike(long id, long userId) {
        userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(id);
        film.getLikes().remove(userId);
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> getBestFilms(int count) {
        return filmStorage.getBestFilms(count);
    }

    public List<Film> getFilms() {
        return filmStorage.getAllFilms();
    }


    /**
     * Возвращает список фильмов, которые понравились обоим пользователям.
     *
     * @param userId   идентификатор первого пользователя.
     * @param friendId идентификатор второго пользователя.
     * @return List<Film> возвращает список фильмов, которые понравились обоим пользователям.
     * @throws NotFoundException если пользователь с указанным идентификатором не найден.
     */
    public List<Film> getCommonFilms(int userId, int friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void add(Film film) {
        validateFilm(film);
        filmStorage.add(film);
    }

    public Film update(Film film) {
        if (getFilmById(film.getId()) == null) {
            throw new NotFoundException("film с id = " + film.getId() + " не найден");
        }
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

    public void deleteFilm(long filmId) {
        filmStorage.deleteFilm(filmId);
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
