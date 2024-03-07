package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    List<User> getUsers();

    boolean isAlreadyExists(long id);

    User update(User user);

    User getUserById(long id);

    User addFriend(long id, long friendId);

    User deleteFriend(long id, long friendId);

    void deleteUser(long userId);

    List<User> getUsersFriends(long id);

    List<User> getCommonFriends(long id, long otherId);

}
