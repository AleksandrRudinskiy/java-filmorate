package ru.yandex.practicum.filmorate.dao;

import lombok.*;

@Getter
public class UserDbFriends {
    private final long userId;
    private final long friendId;

    public UserDbFriends(long userId, long friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
}
