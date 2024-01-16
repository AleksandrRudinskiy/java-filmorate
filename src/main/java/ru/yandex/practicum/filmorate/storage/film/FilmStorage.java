package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    List<Film> getAllFilms();

    boolean isAlreadyExists(long id);

    Film update(Film film);

    List<Film> getBestFilms(int count);

    Film getFilmById(long id);

}
