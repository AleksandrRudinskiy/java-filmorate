package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DirectorDao {
    int add(Director director);

    Collection<Director> findAll();

    Director findById(long id);

    void updateDirector(Director director);

    void deleteDirector(long directorId);

    void deleteFilmDirector(long filmId, long directorId);

    void addFilmDirector(long filmId, long directorId);

    List<Director> getFilmDirectors(long filmId);
}
