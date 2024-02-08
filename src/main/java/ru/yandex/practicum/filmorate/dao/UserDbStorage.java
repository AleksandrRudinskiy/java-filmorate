package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserStorage implements ru.yandex.practicum.filmorate.storage.user.UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        String sql = "insert into users values()";
        return null;
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public boolean isAlreadyExists(long id) {
        return false;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public User getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        if (userRows.next()) {
       
            User user = new User(
                    userRows.getLong("user_id"),
                    userRows.getString("name"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                   userRows.getDate("birthday").toLocalDate(),
                    new HashSet<>()
                    );
             // сейчас возращается пустой список друзей - НАДО ЗАПОЛНИТЬ ЕГО ИЗ БД!

            log.info("Найден пользователь: {} {}", user.getId(), user.getName());

            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
    }
}
