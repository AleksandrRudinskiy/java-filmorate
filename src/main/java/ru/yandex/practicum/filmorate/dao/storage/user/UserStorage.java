package ru.yandex.practicum.filmorate.dao.storage.user;

import ru.yandex.practicum.filmorate.dao.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    List<User> getUsers();

    boolean isAlreadyExists(long id);

    User update(User user);

    User getUserById(long id);

    User addFriend(long id, long friendId);

    User deleteFriend(long id, long friendId);

    List<User> getUsersFriends(long id);

    List<User> getCommonFriends(long id, long otherId);

}
