package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Film add(Film film);

    List<Film> getFilms();

    boolean isAlreadyExists(Film film);

    Film update(Film film);

    Map<Long, Film> getFilmsMap();

    List<Film> getBestFilms(int count);

    Film deleteLike(Film film, long userId);

    Film addLike(Film film, long userId);

    Film getFilmById(int id);

}
