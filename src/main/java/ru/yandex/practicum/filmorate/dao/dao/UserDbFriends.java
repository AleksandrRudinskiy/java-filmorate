package ru.yandex.practicum.filmorate.dao.dao;

import lombok.Getter;

@Getter
public class UserDbFriends {
    private final long userId;
    private final long friendId;

    public UserDbFriends(long userId, long friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
}
