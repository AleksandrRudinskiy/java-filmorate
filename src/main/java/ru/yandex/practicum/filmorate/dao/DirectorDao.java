package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

public interface DirectorDao {
    int add(Director director);

    Collection<Director> findAll();

    Director findById(long id);

    void updateDirector(Director director);

    void deleteDirector(long directorId);

    void deleteFilmDirector(long filmId, long directorId);

    List<Director> getFilmDirectors(long filmId);

    void checkExists(long id);

    boolean doAllDirectorsExist(List<Long> directorIds);

    void addFilmDirectorsBatch(long filmId, List<Long> directorIds);

}
