package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User add(User user);

    List<User> getUsers();

    boolean isAlreadyExists(User user);

    User update(User user);

    Map<Long, User> getUsersMap();

    User getUserById(long id);

}
