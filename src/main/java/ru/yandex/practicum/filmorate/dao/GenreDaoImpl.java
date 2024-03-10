package ru.yandex.practicum.filmorate.dao;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.rowset.*;
import org.springframework.stereotype.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.*;
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", id);
        if (userRows.next()) {
            Genre genre = new Genre(
                    userRows.getInt("genre_id"),
                    userRows.getString("genre_name")
            );
            log.info("Найден жанр фильма: {} {}", genre.getId(), genre.getName());
            return genre;
        } else {
            log.info("Жанр фильма с id = {} не найден.", id);
            throw new NotFoundException("Жанр фильма с id = " + id + " не найден.");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "select * from genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }
}
