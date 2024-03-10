package ru.yandex.practicum.filmorate.service;

import lombok.*;
import org.springframework.stereotype.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.user.*;

import java.time.*;
import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void add(User user) {
        validateUser(user);
        userStorage.add(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User update(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (userStorage.isAlreadyExists(user.getId())) {
            return userStorage.update(user);
        } else if (!userStorage.isAlreadyExists(user.getId()) && (user.getId() != 0)) {
            throw new NotFoundException("Пользователя с id = " + user.getId() + " нет.");
        } else {
            return userStorage.add(user);
        }
    }

    public User addFriend(long id, long friendId) {
        userStorage.isAlreadyExists(id);
        userStorage.isAlreadyExists(friendId);
        return userStorage.addFriend(id, friendId);
    }

    public User deleteFriend(long id, long friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        userStorage.isAlreadyExists(id);
        userStorage.isAlreadyExists(otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    public User getUserById(long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователя с id " + id + "нет");
        }
        return user;
    }

    public List<User> getUsersFriends(long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        }
        return userStorage.getUsersFriends(id);
    }


    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }

    public List<Film> getRecommendations(long id) {
        return userStorage.getRecommendations(id);
    }

    private void validateUser(User user) {
        if ((user.getEmail().isEmpty()) || (!user.getEmail().contains("@"))) {
            throw new ValidationException("E-mail is empty or not contains symbol \"@\"");
        }
        if ((user.getLogin().isEmpty()) || user.getLogin().contains(" ")) {
            throw new ValidationException("Login is empty or contains a space");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth cannot be in the future");
        }
    }
}
