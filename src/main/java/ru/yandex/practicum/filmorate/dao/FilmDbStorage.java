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
        log.info("выполнен метод add");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        log.info("id фильма = " + id);

        int categoryId = 0;
        if (film.getMpa() != null) {
            categoryId = film.getMpa().getId();
            film.setMpa(findMpaByCategoryId(categoryId));
            String sql = "INSERT INTO category (category_id, category_name) values(?, ' ')";
            jdbcTemplate.update(sql, categoryId);
        }

        List<Genre> genres = film.getGenres();
        if (!genres.isEmpty()) {
            for (Genre genre : genres) {
                String sqlGenre = "INSERT INTO genre SELECT ?, ' ' WHERE NOT EXISTS (SELECT genre_id FROM genre WHERE genre_id = ?)";
                jdbcTemplate.update(sqlGenre, genre.getId(), genre.getId());

                String sqlFilmGenre = "insert into film_genre (film_id, genre_id) values (?, ?)";
                jdbcTemplate.update(sqlFilmGenre, id, genre.getId());
            }
        }

        log.info("ПОЛУЧЕН СПИСОК ЖАНРОВ ФИЛЬМА: " + film.getGenres());
        film.setLikes(new HashSet<>(findLikesByFilmId(id)));
        film.setGenres(new ArrayList<>(findGenresByFilmId(id)));

        // update поля category_id
        jdbcTemplate.update("UPDATE films SET category_id = ? WHERE film_id = ?", categoryId, id);
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
                new ArrayList<>(findGenresByFilmId(id)),
                findMpaByCategoryId(rs.getInt("category_id")) // дожжен быть id КАТЕГОРИИ а не фильма
        );
        return film;
    }

    public Collection<Long> findLikesByFilmId(long filmId) {
        String sql = "select user_id from user_likes where film_id = ? order by user_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    public Collection<Genre> findGenresByFilmId(long filmId) {
        String sql = "select * from (select genre_id from film_genre where film_id = ? order by genre_id) as t join genre as g on t.genre_id = g.genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), filmId);
    }

    public Mpa findMpaByCategoryId(long categoryId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from category where category_id = ?", categoryId);
        if (userRows.next()) {
            Mpa mpa = new Mpa(
                    userRows.getInt("category_id"),
                    userRows.getString("category_name")
            );
            log.info("Найдена категория (MPA): {} {}", mpa.getId(), mpa.getName());
            return mpa;
        } else {
            log.info("Категория (MPA) с идентификатором {} не найдена.", categoryId);
            return new Mpa(0, "");
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
                    new ArrayList<>(findGenresByFilmId(id)),
                    findMpaByCategoryId(userRows.getInt("category_id"))
            );
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            return null;
        }
    }

}