package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

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

}
