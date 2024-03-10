package ru.yandex.practicum.filmorate.service;

import lombok.*;
import org.springframework.stereotype.*;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreDao genreDao;

    public List<Genre> getGenres() {
        return genreDao.getAllGenres();
    }

    public Genre getGenreById(int id) {
        if (genreDao.getGenreById(id) == null) {
            throw new NotFoundException("Жанр с id = " + id + " не найден.");
        }
        return genreDao.getGenreById(id);
    }

}
