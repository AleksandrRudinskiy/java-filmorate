package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

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
