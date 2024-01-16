package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    List<User> getUsers();

    boolean isAlreadyExists(long id);

    User update(User user);

    User getUserById(long id);

}
