package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.NotFoundException;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public List<User> findAllUsers() {
        List<User> users = userService.getUsers();
        log.info("Accepted GET request to get a list of all users");
        log.debug("Current number of users: {}", users.size());
        return users;
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriendsUser(@PathVariable long id) {
        User user = userService.getUserById(id);
        Map<Long, User> users = userService.getUsersMap();
        Set<Long> friendsId = new HashSet<>(user.getFriends());
        return friendsId.stream().map(users::get).collect(Collectors.toList());
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        User user1 = userService.getUserById(id);
        if (user1 == null) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        }
        User user2 = userService.getUserById(otherId);
        List<Long> commonFriendsId = userService.getCommonFriends(user1, user2);
        Map<Long, User> users = userService.getUsersMap();
        return commonFriendsId.stream().map(users::get).collect(Collectors.toList());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователя с id = " + userId + " нет.");
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(user);
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        userService.add(user);
        return user;
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        if (userService.isAlreadyExists(user)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userService.update(user));
        } else if (!userService.isAlreadyExists(user) && (user.getId() != 0)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(user);
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userService.add(user));
        }
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable long id, @PathVariable long friendId) {
        User user = userService.getUserById(id);
        User friendUser = userService.getUserById(friendId);
        if (user != null && friendUser != null) {
            userService.addFriend(friendUser, id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userService.addFriend(user, friendId));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(user);
        }
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public Optional<User> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        Optional<User> user = userService.getUsers().stream()
                .filter(item -> item.getId() == id)
                .findFirst();
        return user.map(value -> userService.deleteFriend(value, friendId));
    }
}
