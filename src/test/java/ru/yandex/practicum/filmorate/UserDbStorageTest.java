package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @AfterEach
    public void clear() {
        List<User> users = userStorage.getUsers();
        users.forEach(u -> userStorage.deleteUser(u.getId()));
    }

    @Test
    public void testAddUser() {
        User newUser = new User(0L, "Ivan_Petrov", "ip@email.ru", "ip123", LocalDate.of(1990, 1, 1));
        userStorage.add(newUser);
        assertThat(newUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userStorage.getUserById(1));
    }

    @Test
    public void testAddFriends() {
        User firstUser = new User(0L, "Ivan_Petrov", "ip@email.ru", "ip123", LocalDate.of(1990, 1, 1));
        User secondUser = new User(0L, "Petia", "r@email.ru", "r123", LocalDate.of(1991, 2, 3));
        userStorage.add(firstUser);
        userStorage.add(secondUser);
        User user = userStorage.addFriend(firstUser.getId(), secondUser.getId());
        Assertions.assertEquals(1, user.getFriends().size(), "Кол-во друзей должно быть 1.");
    }

    @Test
    public void testFindUserById() {
        User newUser = new User(0L, "IvanPet", "euser@email.ru", "euser123", LocalDate.of(1990, 1, 1));
        userStorage.add(newUser);
        User savedUser = userStorage.getUserById(newUser.getId());
        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testDeleteFriends() {
        User firstUser = new User(0L, "Ivan_Petrov", "ip@email.ru", "ip123", LocalDate.of(1990, 1, 1));
        User secondUser = new User(0L, "Petia", "r@email.ru", "r123", LocalDate.of(1991, 2, 3));
        userStorage.add(firstUser);
        userStorage.add(secondUser);
        User user = userStorage.addFriend(firstUser.getId(), secondUser.getId());
        Assertions.assertEquals(1, user.getFriends().size(), "Кол-во друзей должно быть 1.");
        User alongUser = userStorage.deleteFriend(firstUser.getId(), secondUser.getId());
        Assertions.assertEquals(0, alongUser.getFriends().size(), "Кол-во друзей должно быть 0 после удаления.");
    }

    @Test
    public void testGetUserById() {
        User newUser = new User(0L, "Iivan_Petrov", "ipi@email.ru", "ipi123", LocalDate.of(1990, 1, 1));
        userStorage.add(newUser);
        User user = userStorage.getUserById(newUser.getId());
        assertThat(newUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    public void testGetAllUsers() {
        User firstUser = new User(0L, "fjvner", "ip@email.ru", "ip123", LocalDate.of(1990, 1, 1));
        User secondUser = new User(0L, "rflenjrj", "r@email.ru", "r123", LocalDate.of(1991, 2, 3));
        User thirdUser = new User(0L, "rfn;erkf", "r@2email.ru", "rwe123", LocalDate.of(1991, 2, 3));
        userStorage.add(firstUser);
        userStorage.add(secondUser);
        userStorage.add(thirdUser);
        List<User> userList = List.of(firstUser, secondUser, thirdUser);
        Assertions.assertEquals(3, userStorage.getUsers().size());
        assertThat(userList)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userStorage.getUsers());
    }
}
