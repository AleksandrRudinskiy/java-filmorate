package ru.yandex.practicum.filmorate.dao;

import lombok.*;

@Getter
public class FilmsDbGenres {
    private final long filmId;
    private final Integer genreId;

    public FilmsDbGenres(long filmId, Integer genreId) {
        this.filmId = filmId;
        this.genreId = genreId;
    }

}
