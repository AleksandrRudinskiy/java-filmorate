package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

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
        userStorage.checkExists(user.getId());
        return userStorage.update(user);
    }

    public User addFriend(long id, long friendId) {
        userStorage.checkExists(id);
        userStorage.checkExists(friendId);
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
        userStorage.checkExists(id);
        return userStorage.getUserById(id);
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

    public List<Event> getFeed(long userId) {
        return userStorage.getFeed(userId);
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
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
