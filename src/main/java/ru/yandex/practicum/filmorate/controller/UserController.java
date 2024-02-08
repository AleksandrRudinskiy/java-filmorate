package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Data
public class UserController {


    private final UserService userService;

    @GetMapping("/users")
    public List<User> findAllUsers() {
        log.info("Accepted GET request to get a list of all users");
        List<User> users = userService.getUsers();
        log.debug("Current number of users: {}", users.size());
        return users;
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUsersFriends(@PathVariable long id) {
        return userService.getUsersFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserById(userId));
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        userService.add(user);
        return user;
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.update(user));
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable long id, @PathVariable long friendId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.addFriend(id, friendId));
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.deleteFriend(id, friendId));

    }
}