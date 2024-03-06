package ru.yandex.practicum.filmorate.dao.dao;

import lombok.Getter;

@Getter
public class FilmsDbGenres {
    private long filmId;
    private Integer genreId;

    public FilmsDbGenres(long filmId, Integer genreId) {
        this.filmId = filmId;
        this.genreId = genreId;
    }

}
