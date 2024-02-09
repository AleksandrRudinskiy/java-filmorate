package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {


    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
        getUserById(friendId);
        return userStorage.addFriend(id, friendId);
    }

    public User deleteFriend(long id, long friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        User firstUser = getUserById(id);
        if (firstUser == null) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        }
        User secondUser = getUserById(otherId);
        if (secondUser == null) {
            throw new NotFoundException("Пользователя с id = " + otherId + " нет.");
        }
        return firstUser.getFriends()
                .stream()
                .filter(f -> secondUser.getFriends().contains(f))
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public User getUserById(long id) {
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException("Пользователя с id " + id + "нет");
        }

        return userStorage.getUserById(id);
    }

    public List<User> getUsersFriends(long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        }
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
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
