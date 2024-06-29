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
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Component
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final EventDao eventDaoImpl;

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
        String sql = "SELECT DISTINCT u.user_id, u.name , u.email , u.login , u.birthday , uf.friend_id from users u " +
                "LEFT JOIN user_friends uf on u.user_id = uf.user_id";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        Map<Long, User> userMap = new HashMap<>();
        users.forEach(u -> userMap.put(u.getId(), u));
        List<UserDbFriends> userDbFriends = jdbcTemplate.query(sql, (rs, rowNum) -> makeUserDbFriends(rs));
        Map<Long, Set<Long>> userFriends = new HashMap<>();
        userDbFriends.forEach(u -> {
            userFriends.put(u.getUserId(), new HashSet<>());
            if (userFriends.containsKey(u.getUserId()) && u.getFriendId() != 0) {
                userFriends.get(u.getUserId()).add(u.getFriendId());
            }
        });
        List<User> users1 = new ArrayList<>(userMap.values());
        users1.forEach(u -> u.setFriends(userFriends.get(u.getId())));
        return users1;
    }

    private UserDbFriends makeUserDbFriends(ResultSet rs) throws SQLException {
        return new UserDbFriends(rs.getLong("user_id"), rs.getLong("friend_id"));
    }

    @Override
    public User update(User user) {
        String sql = "update users set name = ?, email = ?, login = ?, birthday = ? where user_id = ? ";
        jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        return getUserById(user.getId());
    }

    @Override
    public User getUserById(long id) {
        checkExists(id);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
        User user = null;
        if (userRows.next()) {
            user = new User(
                    userRows.getLong("user_id"),
                    userRows.getString("name"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate()
            );
            user.setFriends(new HashSet<>(findFriendsByUserId(id)));
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
        }
        return user;
    }

    @Override
    public User addFriend(long id, long friendId) {
        String sql = "insert into user_friends values (?, ?, false)";
        jdbcTemplate.update(sql, id, friendId);
        Event event = new Event((new Timestamp(System.currentTimeMillis())).getTime(),
                id,
                EventType.FRIEND,
                Operation.ADD,
                friendId);
        eventDaoImpl.add(event);
        return getUserById(id);
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        String sql = "delete from user_friends where user_id  = ? and friend_id = ?";
        jdbcTemplate.update(sql, id, friendId);
        eventDaoImpl.add(new Event((new Timestamp(System.currentTimeMillis())).getTime(),
                id,
                EventType.FRIEND,
                Operation.REMOVE,
                friendId)
        );
        return getUserById(id);
    }

    /**
     * Удаляет пользователя из базы данных на основе их уникального идентификатора.
     *
     * @param userId Уникальный идентификатор пользователя, которого нужно удалить.
     * @throws NotFoundException Если пользователь с идентификатором не существует.
     */
    @Override
    public void deleteUser(long userId) {
        checkExists(userId);
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    private List<Long> findFriendsByUserId(long userId) {
        String sql = "SELECT friend_id FROM user_friends WHERE user_id = ? ORDER BY friend_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id"), userId);
    }

    @Override
    public List<User> getUsersFriends(long id) {
        String sql = "select * from users where user_id in (select friend_id from user_friends where user_id = ? order by friend_id)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        checkExists(id);
        checkExists(otherId);
        String sql = "select * from users " +
                "where user_id = " +
                "(select  uf.friend_id from " +
                "(select friend_id from user_friends where user_id = ?) as tt " +
                "inner  join (select friend_id from user_friends where user_id = ?) uf on uf.friend_id = tt.friend_id)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id, otherId);
    }

    /**
     * Метод для получения рекомендаций фильмов для пользователя.
     *
     * @param id идентификатор пользователя, для которого требуются рекомендации.
     * @return Список рекомендованных фильмов. Если нет подходящих рекомендаций, возвращает пустой список.
     * @throws NotFoundException выбрасывает исключение при неверно переданном идентификаторе.
     *
     *                           <p>Этот метод работает следующим образом:
     *                           <ul>
     *                           <li>Сначала он получает список фильмов, которые понравились каждому пользователю.</li>
     *                           <li>Затем он находит пользователя с наибольшим количеством общих предпочтений с целевым пользователем.</li>
     *                           <li>Наконец, он возвращает список фильмов, которые понравились этому пользователю, но которые целевой пользователь еще не видел.</li>
     *                           </ul>
     */

    public List<Film> getRecommendations(long id) {
        checkExists(id);
        Map<Long, List<Long>> usersLikes = new HashMap<>();
        String sql = "SELECT * FROM user_likes";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sql);
        while (srs.next()) {
            Long userId = srs.getLong("user_id");
            Long filmId = srs.getLong("film_id");
            if (!usersLikes.containsKey(userId)) {
                usersLikes.put(userId, new ArrayList<>());
            }
            usersLikes.get(userId).add(filmId);
        }
        List<Long> thisUserLikes = usersLikes.get(id);
        usersLikes.remove(id);
        Long userIdSecond = -1L;
        long maxCountIntersections = -1L;
        for (Long userId : usersLikes.keySet()) {
            Long countIntersections = findIntersectionsTwoSets(thisUserLikes, usersLikes.get(userId));
            if (maxCountIntersections < countIntersections) {
                maxCountIntersections = countIntersections;
                userIdSecond = userId;
            }
        }
        if (userIdSecond < 1) {
            return new ArrayList<>();
        }
        List<Long> otherUserLikes = usersLikes.get(userIdSecond);
        log.info(otherUserLikes.toString());
        log.info(thisUserLikes.toString());
        otherUserLikes.removeAll(thisUserLikes);

        return otherUserLikes.stream()
                .map(filmStorage::getFilmById)
                .collect(Collectors.toList());
    }

    public List<Event> getFeed(long userId) {
        String sql = "SELECT * FROM events WHERE user_id = ?";
        getUserById(userId);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), userId);
    }

    @Override
    public void checkExists(long id) {
        String sql = "SELECT user_id FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        long result = 0;
        if (userRows.next()) {
            result = userRows.getLong("user_id");
        }
        if (result == 0) {
            throw new NotFoundException("Пользоватея с id = " + id + " не найдено.");
        }
    }

    /**
     * Находит количество элементов в пересечении двух множеств.
     *
     * @param listOne    Первое множество в виде списка.
     * @param listSecond Второе множество в виде списка.
     * @return Количество элементов в пересечении множеств.
     */
    private Long findIntersectionsTwoSets(List<Long> listOne, List<Long> listSecond) {
        Set<Long> setOne = new HashSet<>(listOne);
        Set<Long> setSecond = new HashSet<>(listSecond);
        setOne.retainAll(setSecond);
        return (long) setOne.size();
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

    private Event makeEvent(ResultSet rs) throws SQLException {
        return new Event(rs.getTimestamp("time_stamp").getTime(),
                rs.getLong("user_id"),
                EventType.valueOf(rs.getString("event_type")),
                Operation.valueOf(rs.getString("operation")),
                rs.getLong("event_id"),
                rs.getLong("entity_id")
        );
    }
}
