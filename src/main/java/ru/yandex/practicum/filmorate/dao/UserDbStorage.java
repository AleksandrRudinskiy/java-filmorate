package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        long id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(id);
        user.setFriends(new HashSet<>(findFriendsByUserId(id)));
        return user;
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        long userId = rs.getLong("user_id");
        return new User(
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("login"),
                Objects.requireNonNull(rs.getDate("birthday")).toLocalDate(),
                new HashSet<>(findFriendsByUserId(userId))
        );
    }


    @Override
    public boolean isAlreadyExists(long id) {
        return getUserById(id) != null;
    }

    @Override
    public User update(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        String sql = "update users set name = ?, email = ?, login = ?, birthday = ? WHERE user_id = ? ";
        jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
        if (userRows.next()) {
            User user = new User(
                    userRows.getLong("user_id"),
                    userRows.getString("name"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate(),
                    new HashSet<>(findFriendsByUserId(id))
            );
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
    }


    public Collection<Long> findFriendsByUserId(long userId) {
        String sql = "select friend_id from user_friends where user_id = ? order by friend_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id"), userId);
    }

    @Override
    public User addFriend(long id, long friendId) {
        String sql = "insert into user_friends (user_id, friend_id, friendship) values (?, ?, 'неподтверждена')";
        jdbcTemplate.update(sql, id, friendId);
        return getUserById(id);
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        String sql = "DELETE FROM user_friends WHERE USER_ID  = ? AND friend_id = ?";
        jdbcTemplate.update(sql, id, friendId);
        return getUserById(id);
    }
}
