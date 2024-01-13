package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

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
    public List<User> getUsersFriends(@PathVariable long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        }
        return userService.getUsersFriends(user);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        User firstUser = userService.getUserById(id);
        if (firstUser == null) {
            throw new NotFoundException("Пользователя с id = " + id + " нет.");
        }
        User secondUser = userService.getUserById(otherId);
        if (secondUser == null) {
            throw new NotFoundException("Пользователя с id = " + otherId + " нет.");
        }
        return userService.getCommonFriends(firstUser, secondUser);
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
            throw new NotFoundException("Пользователя с id = " + user.getId() + " нет.");
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userService.add(user));
        }
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable long id, @PathVariable long friendId) {
        User user = userService.getUserById(id);
        User userFriend = userService.getUserById(friendId);
        if (user != null && userFriend != null) {
            userService.addFriend(userFriend, id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userService.addFriend(user, friendId));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(user);
        }
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        User user = userService.getUserById(id);
        User userFriend = userService.getUserById(friendId);
        if (user != null && userFriend != null) {
            userService.deleteFriend(user, friendId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userService.deleteFriend(user, friendId));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(user);
        }
    }
}