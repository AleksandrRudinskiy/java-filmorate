package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Getter
@AllArgsConstructor
public class UserService {
    UserStorage userStorage;

    public User add(User user) {
        return userStorage.add(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User update(User user) {
        if (isAlreadyExists(user)) {
            return userStorage.update(user);
        } else if (!isAlreadyExists(user) && (user.getId() != 0)) {
            throw new NotFoundException("Пользователя с id = " + user.getId() + " нет.");
        } else {
            return userStorage.add(user);
        }
    }

    public boolean isAlreadyExists(User user) {
        return userStorage.isAlreadyExists(user);
    }

    public Map<Long, User> getUsersMap() {
        return userStorage.getUsersMap();
    }

    public User addFriend(long id, long friendId) {
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
                .map(i -> userStorage.getUserById(i))
                .collect(Collectors.toList());
    }

    public User getUserById(long id) {

        return userStorage.getUserById(id);
    }

    public List<User> getUsersFriends(long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        }
        return user.getFriends().stream()
                .map(i -> userStorage.getUserById(i))
                .collect(Collectors.toList());
    }
}
