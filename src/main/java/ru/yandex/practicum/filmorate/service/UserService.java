package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Getter
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService() {
        userStorage = new InMemoryUserStorage();
    }

    public User addFriend(User user, long friendId) {
        Set<Long> updateFriends = new HashSet<>(user.getFriends());
        updateFriends.add(friendId);
        user.setFriends(updateFriends);
        return user;
    }

    public User deleteFriend(User user, long friendId) {
        Set<Long> updateFriends = new HashSet<>(user.getFriends());
        updateFriends.remove(friendId);
        user.setFriends(updateFriends);
        return user;
    }

    public List<Long> getCommonFriens(User user1, User user2) {
        return user1.getFriends()
                .stream()
                .filter(f -> user2.getFriends().contains(f))
                .collect(Collectors.toList());
    }

}
