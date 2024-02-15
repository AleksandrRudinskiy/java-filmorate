package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        log.info("выполнен метод add");
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(filmId);
        log.info("id фильма = " + filmId);
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlFilmGenre = "insert into film_genre (film_id, genre_id) values (?, ?)";
                jdbcTemplate.update(sqlFilmGenre, filmId, genre.getId());
            }
        }
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        return new Film(
                rs.getLong("film_id"),
                rs.getString("film_name"),
                rs.getString("description"),
                Objects.requireNonNull(rs.getDate("release_date")).toLocalDate(),
                rs.getInt("duration"),
                findMpaByCategoryId(rs.getInt("category_id")),
                new ArrayList<>(findGenresByFilmId(id))
        );
    }

    public Collection<Genre> findGenresByFilmId(long filmId) {
        String sql = "select distinct * from (select genre_id from film_genre where film_id = ? order by genre_id) as t join genre as g on t.genre_id = g.genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), filmId);
    }

    public Mpa findMpaByCategoryId(long categoryId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from category where category_id = ?", categoryId);
        if (userRows.next()) {
            Mpa mpa = new Mpa(
                    userRows.getInt("category_id"),
                    userRows.getString("category_name")
            );
            log.info("Найдена категория (MPA) фильма: {}", mpa.getId());
            return mpa;
        } else {
            log.info("Категория (MPA) с идентификатором {} не найдена.", categoryId);
            throw new NotFoundException("Категория (MPA) с идентификатором" + categoryId + "не найдена.");
        }
    }

    @Override
    public boolean isAlreadyExists(long id) {
        return getFilmById(id) != null;
    }

    @Override
    public Film update(Film film) {
        String sql = "update films set film_name = ?, description = ?, release_date = ?, duration = ?, category_id = ? where film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        long id = film.getId();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlFilmGenre = "insert into film_genre (film_id, genre_id) values (?, ?)";
                jdbcTemplate.update(sqlFilmGenre, id, genre.getId());
            }
            Set<Integer> idUpdateGenres = film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
            Set<Integer> idOldGenres = findGenresByFilmId(id).stream().map(Genre::getId).collect(Collectors.toSet());
            idOldGenres.removeAll(idUpdateGenres);
            for (Integer deleteGenreId : idOldGenres) {
                String newSql = "delete from film_genre where film_id = ? and genre_id = ? ";
                jdbcTemplate.update(newSql, id, deleteGenreId);
            }
        }
        return getFilmById(id);
    }

    @Override
    public List<Film> getBestFilms(int count) {
        String sql = "select f.film_id from films as f left join user_likes as ul on f.film_id  = ul.film_id group by f.film_id order by count(user_id) desc limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> getFilmById(rs.getInt("film_id")), count);
    }

    @Override
    public Film getFilmById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from films where film_id = ?", id);
        if (userRows.next()) {
            Film film = new Film(
                    userRows.getLong("film_id"),
                    userRows.getString("film_name"),
                    userRows.getString("description"),
                    Objects.requireNonNull(userRows.getDate("release_date")).toLocalDate(),
                    userRows.getInt("duration"),
                    findMpaByCategoryId(userRows.getInt("category_id")),
                    new ArrayList<>(findGenresByFilmId(id))
            );
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Фильм с id = " + id + "не найден.");
        }
    }

    @Override
    public Film addLike(long id, long userId) {
        String sql = "insert into user_likes values(?, ?)";
        jdbcTemplate.update(sql, id, userId);
        return getFilmById(id);
    }
}
