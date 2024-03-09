package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {

    Film add(Film film);

    List<Film> getAllFilms();

    List<Film> getCommonFilms(int userId, int friendId);

    boolean isAlreadyExists(long id);

    Film update(Film film);

    List<Film> getBestFilms(int genreId, int year, int count);

    List<Film> getBestFilmsWithGenre(int genreId);

    Set<Film> getBestFilmsWithYear(int year);

    List<Film> getBestFilms(int count);

    Film getFilmById(long id);

    Film addLike(long id, long userId);

    void deleteFilm(long filmId);

    Film deleteLike(long id, long userId);

}

