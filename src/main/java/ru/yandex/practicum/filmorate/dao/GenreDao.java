package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface GenreDao {

    Genre getGenreById(int id);

    List<Genre> getAllGenres();

}
