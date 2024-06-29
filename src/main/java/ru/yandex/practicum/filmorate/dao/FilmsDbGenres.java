package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilmsDbGenres {
    private final long filmId;
    private final Integer genreId;

}
