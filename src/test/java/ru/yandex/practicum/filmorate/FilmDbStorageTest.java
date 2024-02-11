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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    void testAddFilm() {
        Film newFilm = new Film(1L, "Some Film", "bla bla bla", LocalDate.of(2000, 12, 1), 98, new Mpa(1), null, null);
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.add(newFilm);
        Assertions.assertEquals(1, filmDbStorage.getAllFilms().size(), "Количество фильмов должно быть 1!");
    }

    @Test
    void testUpdateFilm() {
        Film newFilm = new Film(2L, "Some Film", "bla bla bla", LocalDate.of(2000, 12, 1), 98, new Mpa(1), new ArrayList<>(), new HashSet<>());
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.add(newFilm);
        Film updateFilm = new Film(2L, "Some New Film", "BBBbla bla bla", LocalDate.of(2000, 12, 1), 98, new Mpa(2), new ArrayList<>(), new HashSet<>());
        filmDbStorage.update(updateFilm);
        assertThat(updateFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmDbStorage.getFilmById(updateFilm.getId()));
    }

    @Test
    void testGetFilmById() {
        Film newFilm = new Film(3L, "Some Film", "bla bla bla", LocalDate.of(2000, 12, 1), 98, new Mpa(1), new ArrayList<>(), new HashSet<>());
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.add(newFilm);
        assertThat(newFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmDbStorage.getFilmById(3));
    }

    @Test
    void testAddLikeToFilm() {
        Film newFilm = new Film(4L, "Some Film", "bla bla bla", LocalDate.of(2000, 12, 1), 98, new Mpa(1), new ArrayList<>(), new HashSet<>());
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.add(newFilm);
        User newUser = new User(1L, "IvanPetrov", "euser@email.ru", "euser123", LocalDate.of(1990, 1, 1), new HashSet<>());
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userDbStorage.add(newUser);
        filmDbStorage.addLike(newFilm.getId(), newUser.getId());
        Assertions.assertEquals(1, filmDbStorage.getFilmById(newFilm.getId()).getLikes().size(), "Число лайков должно быть 1!");
    }
}
