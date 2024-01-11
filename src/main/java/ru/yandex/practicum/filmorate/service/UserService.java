package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Map;

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
        return userStorage.update(user);
    }

    public boolean isAlreadyExists(User user) {
        return userStorage.isAlreadyExists(user);
    }

    public Map<Long, User> getUsersMap() {
        return userStorage.getUsersMap();
    }

    public User addFriend(User user, long friendId) {
        return userStorage.addFriend(user, friendId);
    }

    public User deleteFriend(User user, long friendId) {
        return userStorage.deleteFriend(user, friendId);
    }

    public List<Long> getCommonFriends(User user1, User user2) {
        return userStorage.getCommonFriends(user1, user2);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }
}
