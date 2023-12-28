package ru.yandex.practicum.filmorate.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 1;

    public User add(User user) {
        if (user.getId() == 0) {
            user.setId(currentId);
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        currentId++;
        return user;
    }

    public boolean isAlreadyExists(User user) {
        return users.containsKey(user.getId());
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        }
        return user;
    }

}
