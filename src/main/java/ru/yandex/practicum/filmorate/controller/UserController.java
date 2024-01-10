package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAllUsers() {
        List<User> users = userService.getUserStorage().getUsers();
        log.info("Accepted GET request to get a list of all users");
        log.debug("Current number of users: {}", users.size());
        return users;
    }

    @GetMapping("/users/{id}/friends")
    public Optional<List<User>> getFriendsUser(@PathVariable long id) {
        Optional<User> optionalUser = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == id)
                .findFirst();
        Map<Long, User> users = userService.getUserStorage().getUsersMap();
        Set<Long> friendsId = new HashSet<>();
        optionalUser.ifPresent(user -> friendsId.addAll(user.getFriends()));
        return Optional.of(friendsId.stream().map(users::get).collect(Collectors.toList()));
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        User user1 = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == id)
                .findFirst().orElse(null);
        User user2 = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == otherId)
                .findFirst().orElse(null);
        assert user1 != null;
        List<Long> commonFriendsId = userService.getCommonFriens(user1, user2);
        Map<Long, User> users = userService.getUserStorage().getUsersMap();
        return commonFriendsId.stream().map(users::get).collect(Collectors.toList());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable int userId) {
        Optional<User> optionalUser = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == userId)
                .findFirst();
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(optionalUser);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(optionalUser);
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        userService.getUserStorage().add(user);
        return user;
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        if (userService.getUserStorage().isAlreadyExists(user)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userService.getUserStorage().update(user));
        } else if (!userService.getUserStorage().isAlreadyExists(user) && (user.getId() != 0)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(user);
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userService.getUserStorage().add(user));
        }
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<Optional<User>> addFriend(@PathVariable long id, @PathVariable long friendId) {
        Optional<User> user = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == id)
                .findFirst();
        Optional<User> friendUser = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == friendId)
                .findFirst();
        if (user.isPresent() && friendUser.isPresent()) {
            userService.addFriend(friendUser.get(), id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Optional.of(userService.addFriend(user.get(), friendId)));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Optional.empty());
        }
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public Optional<User> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        Optional<User> user = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == id)
                .findFirst();
        return user.map(value -> userService.deleteFriend(value, friendId));
    }
}
