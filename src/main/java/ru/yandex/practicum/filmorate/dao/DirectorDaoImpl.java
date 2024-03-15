package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void deleteDirector(long directorId) {
        try {
            deleteDirectorFromFilms(directorId);
            String sqlQuery = "delete from DIRECTOR where DIRECTOR_ID = ?";
            int rowsAffected = jdbcTemplate.update(sqlQuery, directorId);
            if (rowsAffected == 0) {
                throw new NotFoundException("Режисер с идентификатором " + directorId + " не найден.");
            }
        } catch (DataAccessException e) {
            log.error("Error during deleting director with id: {}", directorId, e);
            throw e;
        }
    }

    private void deleteDirectorFromFilms(long directorId) {
        String sqlQuery = "DELETE FROM DIRECTOR_TO_FILM WHERE DIRECTOR_ID=?";

        jdbcTemplate.update(sqlQuery, directorId);
    }

    @Override
    public void deleteFilmDirector(long filmId, long directorId) {
        String sqlQuery = "DELETE FROM DIRECTOR_TO_FILM WHERE FILM_ID=? and DIRECTOR_ID=?";

        jdbcTemplate.update(sqlQuery, filmId, directorId);
    }

    @Override
    public int add(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTOR")
                .usingGeneratedKeyColumns("DIRECTOR_ID");
        return simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue();
    }

    @Override
    public Collection<Director> findAll() {
        String sqlQuery = "select DIRECTOR_ID,DIRECTOR_NAME from DIRECTOR";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director findById(long id) {
        try {
            String sqlQuery = "select DIRECTOR_ID,DIRECTOR_NAME from DIRECTOR where DIRECTOR_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("not found director by id: {}", id);
            return null;
        }
    }

    @Override
    public void updateDirector(Director director) {
        String sqlQuery = "update DIRECTOR set DIRECTOR_NAME=? where DIRECTOR_ID = ?";
        int rowsAffected = jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        if (rowsAffected == 0) {
            throw new NotFoundException("Режисер с идентификатором " + director.getId() + " не найден.");
        }
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new Director(
                resultSet.getInt("DIRECTOR_ID"),
                resultSet.getString("DIRECTOR_NAME")
        );
    }

    @Override
    public List<Director> getFilmDirectors(long filmId) {
        String sqlQuery = "SELECT d.DIRECTOR_ID, d.DIRECTOR_NAME " +
                "FROM DIRECTOR d " +
                "JOIN DIRECTOR_TO_FILM dtf ON d.DIRECTOR_ID = dtf.DIRECTOR_ID " +
                "WHERE dtf.FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, new Object[]{filmId}, this::mapRowToDirector);
    }

    @Override
    public void addFilmDirector(long filmId, long directorId) {
        String sqlQuery = "INSERT INTO DIRECTOR_TO_FILM (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, directorId);
    }

    @Override
    public void checkExists(long id) {
        String sql = "SELECT DIRECTOR_ID FROM DIRECTOR WHERE DIRECTOR_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        long result = 0;
        if (userRows.next()) {
            result = userRows.getLong("DIRECTOR_ID");
        }
        if (result == 0) {
            throw new NotFoundException("Режиссер с id = " + id + " не найден.");
        }
    }
}
