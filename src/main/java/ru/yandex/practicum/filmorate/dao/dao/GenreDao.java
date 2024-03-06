package ru.yandex.practicum.filmorate.dao.dao;

import ru.yandex.practicum.filmorate.dao.model.Genre;

import java.util.List;

public interface GenreDao {

    Genre getGenreById(int id);

    List<Genre> getAllGenres();

}
