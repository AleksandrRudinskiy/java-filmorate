package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Primary
@Component
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();
        user.setId(userId);
        return getUserById(userId);
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT DISTINCT u.USER_ID, u.NAME , u.EMAIL , u.LOGIN , u.BIRTHDAY , uf.FRIEND_ID\n" +
                "FROM USERS u\n" +
                "LEFT JOIN USER_FRIENDS uf ON u.USER_ID = uf.USER_ID";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        Map<Long, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }
        List<UserDbFriends> userDbFriends = jdbcTemplate.query(sql, (rs, rowNum) -> new UserDbFriends(rs.getLong("user_id"), rs.getLong("friend_id")));
        Map<Long, Set<Long>> userFriends = new HashMap<>();
        for (UserDbFriends userDbFriend : userDbFriends) {
            userFriends.put(userDbFriend.getUserId(), new HashSet<>());
            if (userFriends.containsKey(userDbFriend.getUserId()) && userDbFriend.getFriendId() != 0) {
                userFriends.get(userDbFriend.getUserId()).add(userDbFriend.getFriendId());
            }
        }
        List<User> users1 = new ArrayList<>(userMap.values());
        users1.forEach(u -> u.setFriends(userFriends.get(u.getId())));
        return users1;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("login"),
                Objects.requireNonNull(rs.getDate("birthday")).toLocalDate()
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
        String sql = "update users set name = ?, email = ?, login = ?, birthday = ? where user_id = ? ";
        jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        return getUserById(user.getId());
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
                    Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate()
            );
            user.setFriends(new HashSet<>(findFriendsByUserId(id)));
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }

    @Override
    public User addFriend(long id, long friendId) {
        String sql = "insert into user_friends values (?, ?, false)";
        jdbcTemplate.update(sql, id, friendId);
        return getUserById(id);
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        String sql = "delete from user_friends where user_id  = ? and friend_id = ?";
        jdbcTemplate.update(sql, id, friendId);
        return getUserById(id);
    }

    private List<Long> findFriendsByUserId(long userId) {
        String sql = "select friend_id from user_friends where user_id = ? order by friend_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id"), userId);
    }

    @Override
    public List<User> getUsersFriends(long id) {
        String sql = "select * from users where user_id in (select friend_id from user_friends where user_id = ? order by friend_id)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }
}
