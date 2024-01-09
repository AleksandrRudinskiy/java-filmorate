package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.servlet.http.HttpServletResponse;
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
    public Optional<List<User>> getFriendsUser(@PathVariable long id, HttpServletResponse response) {
        Optional<User> optionalUser = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == id)
                .findFirst();
        Map<Long, User> users = userService.getUserStorage().getUsersMap();
        Set<Long> friendsId = optionalUser.get().getFriends();
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
        return  commonFriendsId.stream().map(users::get).collect(Collectors.toList());
    }

    @GetMapping("/users/{userId}")
    public Optional<User> getUserById(@PathVariable int userId, HttpServletResponse response) {
        Optional<User> optionalUser = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == userId)
                .findFirst();
        if (optionalUser.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return optionalUser;
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        userService.getUserStorage().add(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user, HttpServletResponse response) {
        if (userService.getUserStorage().isAlreadyExists(user)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return userService.getUserStorage().update(user);
        } else if (!userService.getUserStorage().isAlreadyExists(user) && (user.getId() != 0)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return user;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return userService.getUserStorage().add(user);
        }
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId, HttpServletResponse response) {
        Optional<User> user = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == id)
                .findFirst();

        Optional<User> friendUser = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == friendId)
                .findFirst();

        if (user.isPresent() && friendUser.isPresent()) {
            userService.addFriend(friendUser.get(), id);
            return userService.addFriend(user.get(), friendId);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return user.get();
        }
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable long id, @PathVariable long friendId, HttpServletResponse response) {
        Optional<User> user = userService.getUserStorage().getUsers().stream()
                .filter(item -> item.getId() == id)
                .findFirst();
        return userService.deleteFriend(user.get(), friendId);
    }
}
