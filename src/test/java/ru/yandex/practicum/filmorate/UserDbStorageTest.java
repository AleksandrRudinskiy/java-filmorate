package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanTable() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        jdbcTemplate.update("DELETE FROM users");
        jdbcTemplate.update("DELETE FROM user_friends");
        jdbcTemplate.update("DELETE FROM user_likes");
    }


    @Test
    public void testAddUser() {
        log.info("testAddUser()");
        User newUser = new User(1L, "IvanrgePetrov", "euserger@email.ru", "euserdfg123", LocalDate.of(1990, 1, 1), new HashSet<>());
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);
        log.info("Кол-во пользователей : {} ", userStorage.getUsers().size());
        Assertions.assertEquals(1, userStorage.getUsers().size(), "Пользователей должно быть: 1!");
    }

    @Test
    public void testAddAndDeleteFriends() {
        User firstUser = new User(3L, "IvanPetrov", "euser@email.ru", "euser123", LocalDate.of(1990, 1, 1), new HashSet<>());
        User secondUser = new User(4L, "Petia", "r@email.ru", "r123", LocalDate.of(1991, 2, 3), new HashSet<>());
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(firstUser);
        userStorage.add(secondUser);
        userStorage.addFriend(firstUser.getId(), secondUser.getId());
        Assertions.assertEquals(1, userStorage.getUserById(firstUser.getId()).getFriends().size(), "Кол-во друзей должно быть 1.");
        userStorage.deleteFriend(firstUser.getId(), secondUser.getId());
        Assertions.assertEquals(0, userStorage.getUserById(firstUser.getId()).getFriends().size(), "Кол-во друзей должно быть 0.");
    }

    @Test
    public void testFindUserById() {
        User newUser = new User(7L, "IvanPet", "euser@email.ru", "euser123", LocalDate.of(1990, 1, 1), new HashSet<>());
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);
        User savedUser = userStorage.getUserById(newUser.getId());
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }
}
