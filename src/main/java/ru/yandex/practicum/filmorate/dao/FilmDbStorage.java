package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {


        if (film.getMpa() != null) {
            int categoryId = film.getMpa().getId();
            String sql = "INSERT INTO category SELECT ?, ' ' WHERE NOT EXISTS (SELECT category_id FROM category WHERE category_id = ?)";
            jdbcTemplate.update(sql, categoryId, categoryId);
        }


       Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                String sql = "INSERT INTO genre SELECT ?, ' ' WHERE NOT EXISTS (SELECT genre_id FROM genre WHERE genre_id = ?)";
                jdbcTemplate.update(sql, genre.getId(), genre.getId());
            }
        }


        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        film.setLikes(new HashSet<>(findLikesByFilmId(id)));
        film.setGenres(new HashSet<>(findGenresByFilmId(id)));
        film.setMpa(findMpaByCategoryId(id).get());
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        Film film = new Film(
                rs.getLong("film_id"),
                rs.getString("film_name"),
                rs.getString("description"),
                Objects.requireNonNull(rs.getDate("releaseDate")).toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(findLikesByFilmId(id)),
                new HashSet<>(findGenresByFilmId(id)),
                findMpaByCategoryId(id).get()
        );

        film.setLikes( new HashSet<>(findLikesByFilmId(id)));
        film.setGenres(new HashSet<>(findGenresByFilmId(id)));
        film.setMpa(findMpaByCategoryId(id).get());

        return  film;
    }

    public Collection<Long> findLikesByFilmId(long filmId) {
        String sql = "select user_id from user_likes where film_id = ? order by user_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    public Collection<Genre> findGenresByFilmId(long filmId) {
        String sql = "select genre_id from film_genre where film_id = ? order by genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre( rs.getInt("genre_id"), rs.getString("name")), filmId);
    }

    public Optional<Mpa> findMpaByCategoryId(long categoryId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from category where category_id = ?", categoryId);
        if (userRows.next()) {
            Mpa mpa = new Mpa(
                    userRows.getInt("category_id"),
                    userRows.getString("name")
            );
            log.info("Найдена категория (MPA): {} {}", mpa.getId(), mpa.getName());
            return Optional.of(mpa);
        } else {
            log.info("Категория (MPA) с идентификатором {} не найдена.", categoryId);
            return Optional.empty();
        }
    }

    @Override
    public boolean isAlreadyExists(long id) {
        return getFilmById(id) != null;
    }

    @Override
    public Film update(Film film) {
        String sql = "update films set film_name = ?, description = ?, releaseDate = ?, duration = ? WHERE film_id = ? ";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        return film;
    }

    @Override
    public List<Film> getBestFilms(int count) {

        return null;
    }

    @Override
    public Film getFilmById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from films where film_id = ?", id);
        if (userRows.next()) {
            Film film = new Film(
                    userRows.getLong("film_id"),
                    userRows.getString("film_name"),
                    userRows.getString("description"),
                    Objects.requireNonNull(userRows.getDate("releaseDate")).toLocalDate(),
                    userRows.getInt("duration"),
                    new HashSet<>(findLikesByFilmId(id)),
                    new HashSet<>(findGenresByFilmId(id)),
                    findMpaByCategoryId(id).get()
            );
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            return null;
        }
    }


}
