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

@Component
@Slf4j
@AllArgsConstructor
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpaById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from category where category_id = ?", id);
        if (userRows.next()) {
            Mpa mpa = new Mpa(
                    userRows.getInt("category_id"),
                    userRows.getString("category_name")
            );
            log.info("Найден рейтинг (категория) фильма: {} {}", mpa.getId(), mpa.getName());
            return mpa;
        } else {
            log.info("Рейтинг (категория) фильма с идентификатором {} не найден.", id);
            throw new NotFoundException("Рейтин с id =" + id + "не найден.");
        }
    }

    @Override
    public List<Mpa> getMpa() {
        String sql = "select * from category";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(rs.getInt("category_id"), rs.getString("category_name"));
    }
}
