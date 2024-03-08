package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testAddFilm() {
        Film newFilm = new Film(1L, "Some Film", "bla bla bla", LocalDate.of(2000, 12, 1), 98, new Mpa(1, "G"), new ArrayList<>());
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.add(newFilm);
        Assertions.assertEquals(1, filmDbStorage.getAllFilms().size(), "Количество фильмов должно быть 1!");
        assertThat(newFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmDbStorage.getFilmById(1));
    }

    @Test
    void testGetFilmById() {
        Film newFilm = new Film(2L, "Some Film", "bla bla bla", LocalDate.of(2000, 12, 1), 98, new Mpa(1, "G"), new ArrayList<>());
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.add(newFilm);
        Film film = filmDbStorage.getFilmById(newFilm.getId());
        assertThat(newFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

}
