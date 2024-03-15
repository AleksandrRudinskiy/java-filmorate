package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorDaoImpl directorDbStorage;
    private final EventDao eventDaoImpl;

    @Override
    public Film add(Film film) {
        log.info("Выполнен метод add");
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

    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.* " +
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
        //Проверяем наличие режиссёра
        directorDbStorage.findById(directorId);

        //Поля сортировки. Легко расширяется без необходимости делать else if
        Map<String, String> validSortFields = Map.of(
                "likes", "(SELECT COUNT(user_id) FROM user_likes WHERE film_id = films.film_id) DESC",
                "year", "films.release_date"
        );

        String sql = "SELECT * FROM films " +
                "JOIN director_to_film ON films.film_id = director_to_film.film_id " +
                "WHERE director_to_film.director_id = ?";

        //Применяем сортировку
        if (validSortFields.containsKey(sortBy)) {
            sql += " ORDER BY " + validSortFields.get(sortBy);
        }
        return jdbcTemplate.query(sql, (rs, rowNum) -> getFilmById(rs.getInt("film_id")), directorId);
    }

    @Override
    public List<Film> getBestFilms(int genreId, int year, int count) {
        String sql = "SELECT f.film_id " +
                "FROM films f " +
                "LEFT JOIN user_likes ul ON f.film_id  = ul.film_id " +
                "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY count(ul.user_id) " +
                "DESC LIMIT ?";
        Genre genre = getGenreById(genreId);
        Set<Film> films = new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> getFilmById(rs.getInt("film_id")), count));
        if (genreId == 0 && year != 0) {
            return films.stream()
                    .filter(f -> f.getReleaseDate().getYear() == year)
                    .sorted((f1, f2) -> Math.toIntExact(f1.getId() - f2.getId()))
                    .collect(Collectors.toList());
        } else if (year == 0 && genreId != 0) {
            log.info("Запрос на топ фильмов с жанром {}", genreId);
            log.info("Искомый жанр {}", genre);
            return films.stream()
                    .filter(f -> f.getGenres().contains(genre))
                    .sorted((f1, f2) -> Math.toIntExact(f1.getId() - f2.getId()))
                    .collect(Collectors.toList());
        } else if (year != 0) {
            return films.stream()
                    .filter(f -> f.getReleaseDate().getYear() == year)
                    .filter(f -> f.getGenres().contains(genre))
                    .sorted((f1, f2) -> Math.toIntExact(f1.getId() - f2.getId()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>(films);
        }
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
                    new ArrayList<>(directorDbStorage.getFilmDirectors(id))
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
        Event event = new Event((new Timestamp(System.currentTimeMillis())).getTime(),
                userId,
                EventType.LIKE,
                Operation.ADD,
                id);
        eventDaoImpl.add(event);
        return getFilmById(id);
    }

    @Override
    public Set<Long> getLikes(long id) {
        String sql = "select user_id from  user_likes where film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, id));
    }

    @Override
    public void deleteFilm(long filmId) {
        checkExists(filmId);
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void checkExists(long filmId) {
        String sql = "select film_id from films where film_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, filmId);
        long result = 0;
        if (userRows.next()) {
            result = userRows.getLong("film_id");
        }
        if (result == 0) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }
    }

    @Override
    public Film deleteLike(long id, long userId) {
        String sql = "DELETE FROM user_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
        Event event = new Event((new Timestamp(System.currentTimeMillis())).getTime(),
                userId,
                EventType.LIKE,
                Operation.REMOVE,
                id);
        eventDaoImpl.add(event);
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
                new ArrayList<>(directorDbStorage.getFilmDirectors(id))
        );
    }

    private Genre getGenreById(int genreId) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, genreId);
        Genre genre = null;
        if (userRows.next()) {
            genre = new Genre(
                    userRows.getInt("genre_id"),
                    userRows.getString("genre_name")
            );
        }
        return genre;
    }
}
