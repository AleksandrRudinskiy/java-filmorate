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

    Film getFilmById(long id);

    Film addLike(long id, long userId);

    Set<Long> getLikes(long id);

    void deleteFilm(long filmId);

    Film deleteLike(long id, long userId);

    List<Film> findAllByDirectorIdSorted(Long directorId, String sortBy);

    void checkExists(long filmId);

}

