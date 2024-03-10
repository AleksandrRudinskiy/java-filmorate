package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;
import java.util.stream.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private int currentId = 1;

    @Override
    public User add(User user) {
        if (user.getId() == 0 && !users.containsValue(user)) {
            user.setId(currentId);
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        currentId++;
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean isAlreadyExists(long id) {
        return users.containsKey(id);
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            if (user.getFriends() == null) {
                user.setFriends(new HashSet<>());
            }
            users.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        }
        return users.get(id);
    }

    @Override
    public User addFriend(long id, long friendId) {
        if (!isAlreadyExists(id)) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        }
        if (!isAlreadyExists(friendId)) {
            throw new NotFoundException("Пользователя с id = " + friendId + " нет.");
        }
        User user = getUserById(id);
        user.getFriends().add(friendId);
        User friendUser = getUserById(friendId);
        friendUser.getFriends().add(id);
        return user;
    }

    @Override
    public User deleteFriend(long id, long friendId) {
        User user = getUserById(id);
        if (isAlreadyExists(id) && isAlreadyExists(friendId)) {
            user.getFriends().remove(friendId);
        } else if (!isAlreadyExists(id)) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        } else {
            throw new NotFoundException("Пользователя с friendId = " + friendId + " нет.");
        }
        user.getFriends().remove(friendId);
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        //TODO
    }

    @Override
    public List<User> getUsersFriends(long id) {
        User user = getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        }
        return user.getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
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
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getRecommendations(long id) {
        //TODO
        return null;
    }
}
