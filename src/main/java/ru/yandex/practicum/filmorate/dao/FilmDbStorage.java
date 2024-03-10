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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final DirectorDaoImpl directorDbstorage;

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
        return jdbcTemplate.query("SELECT * FROM films", this::makeFilm);
    }

    /**
     * Возвращает список фильмов, которые понравились обоим пользователям.
     *
     * @param userId   идентификатор первого пользователя.
     * @param friendId идентификатор второго пользователя.
     * @return List<Film> возвращает список фильмов, которые понравились обоим пользователям.
     */
    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql =
                "SELECT f.* " +
                        "FROM films AS f " +
                        "JOIN user_likes AS ul_1 ON f.film_id = ul_1.film_id " +
                        "JOIN user_likes AS ul_2 ON f.film_id = ul_2.film_id " +
                        "WHERE ul_1.user_id = ? AND ul_2.user_id = ?;";
        return jdbcTemplate.query(sql, this::makeFilm, userId, friendId);
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

    public List<Film> findAllByDirectorIdSorted(Long directorId, String sortBy) {
        if (directorDbstorage.findById(directorId).isEmpty()) {
            throw new NotFoundException("Режиссёр с id = " + directorId + "не найден.");
        }
        String sql = "SELECT * FROM films " +
                "JOIN director_to_film ON films.film_id = director_to_film.film_id " +
                "WHERE director_to_film.director_id = ?";
        if ("likes".equals(sortBy)) {
            sql += " ORDER BY (SELECT COUNT(user_id) FROM user_likes WHERE film_id = films.film_id) DESC";
        } else if ("year".equals(sortBy)) {
            sql += " ORDER BY films.release_date";
        }
        return jdbcTemplate.query(sql, (rs, rowNum) -> getFilmById(rs.getInt("film_id")), directorId);
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
                    new ArrayList<>(findGenresByFilmId(id)),
                    new ArrayList<>(directorDbstorage.getFilmDirectors(id))
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

    /**
     * Удаляет фильм из базы данных по его идентификатору.
     *
     * @param filmId Идентификатор фильма, который нужно удалить.
     * @throws NotFoundException Если фильм с идентификатором filmId не существует.
     */
    @Override
    public void deleteFilm(long filmId) {
        getFilmById(filmId);
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    /**
     * Удаляет лайк пользователя к фильму.
     *
     * @param id     идентификатор фильма, для которого нужно удалить лайк.
     * @param userId идентификатор пользователя, чей лайк нужно удалить.
     * @return Film  возвращает объект фильма, для которого был удален лайк.
     */
    @Override
    public Film deleteLike(long id, long userId) {
        String sql = "DELETE FROM user_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
        return getFilmById(id);
    }

    private Collection<Genre> findGenresByFilmId(long filmId) {
        String sql = "select distinct * from (select genre_id from film_genre where film_id = ? order by genre_id) as t join genre as g on t.genre_id = g.genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"), rs.getString("genre_name")), filmId);
    }

    private Mpa findMpaByCategoryId(long categoryId) {
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

    //FIXME
    // Этот метод где-то используется?
    private FilmsDbGenres makeFilmDbGenres(ResultSet rs) throws SQLException {
        return new FilmsDbGenres(rs.getLong("film_id"), rs.getInt("genre_id"));
    }

    //FIXME
    // Этот метод где-то используется?
    private Optional<Genre> getGenreById(int genreId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", genreId);
        if (genreId == 0) {
            return Optional.empty();
        }
        if (userRows.next()) {
            return Optional.of(new Genre(
                    userRows.getInt("genre_id"),
                    userRows.getString("genre_name")
            ));
        } else {
            throw new NotFoundException("Жанр не найден.");
        }
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("film_id");
        return new Film(
                rs.getLong("film_id"),
                rs.getString("film_name"),
                rs.getString("description"),
                Objects.requireNonNull(rs.getDate("release_date")).toLocalDate(),
                rs.getInt("duration"),
                findMpaByCategoryId(rs.getInt("category_id")),
                new ArrayList<>(findGenresByFilmId(id)),
                new ArrayList<>(directorDbstorage.getFilmDirectors(id))
        );
    }

}
