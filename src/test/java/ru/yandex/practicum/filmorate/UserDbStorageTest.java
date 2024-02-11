package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
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
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testAddUser() {
        User newUser = new User(1L, "IvanPetrov", "euser@email.ru", "euser123", LocalDate.of(1990, 1, 1), new HashSet<>());
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);
        Assertions.assertEquals(1, userStorage.getUsers().size(), "Пользователей должно быть: 1!");
    }

    @Test
    public void testUpdateUser() {
        User user = new User(2L, "IvanPetrov", "euser@email.ru", "euser123", LocalDate.of(1990, 1, 1), new HashSet<>());
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(user);
        User updateUser = new User(2L, "IvanPetrov", "ksser@email.ru", "euser123", LocalDate.of(1989, 2, 15), new HashSet<>());
        userStorage.update(updateUser);
        assertThat(updateUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userStorage.getUserById(user.getId()));
    }

    @Test
    public void testAddAndDeleteFriends() {
        User firstUser = new User(3L, "IvanPetrov", "euser@email.ru", "euser123", LocalDate.of(1990, 1, 1), new HashSet<>());
        User secondUser = new User(4L, "Petia", "r@email.ru", "r123", LocalDate.of(1991, 2, 3), new HashSet<>());
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(firstUser);
        userStorage.add(secondUser);
        userStorage.addFriend(firstUser.getId(), secondUser.getId());
        Assertions.assertEquals(1, userStorage.getUserById(firstUser.getId()).getFriends().size()
                , "Кол-во друзей должно быть 1.");
        userStorage.deleteFriend(firstUser.getId(), secondUser.getId());
        Assertions.assertEquals(0, userStorage.getUserById(firstUser.getId()).getFriends().size()
                , "Кол-во друзей должно быть 0.");

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
