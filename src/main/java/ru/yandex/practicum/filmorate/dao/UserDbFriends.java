package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDbFriends {
    private final long userId;
    private final long friendId;

}
