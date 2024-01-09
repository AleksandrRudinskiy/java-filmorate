package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private int currentId = 1;

    @Override
    public User add(User user) {
        validateUser(user);
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
    public boolean isAlreadyExists(User user) {
        return users.containsKey(user.getId());
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
    public Map<Long, User> getUsersMap() {
        return users;
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
