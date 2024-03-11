package ru.yandex.practicum.filmorate.controller;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.*;

import javax.validation.*;
import java.util.*;

@RestController
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public List<User> findAllUsers() {
        log.info("GET-запрос на получение списка всех пользователей.");
        List<User> users = userService.getUsers();
        log.info("Текущее количество пользователей: {}.", users.size());
        return users;
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUsersFriends(@PathVariable long id) {
        log.info("GET-Запрос на получение друзей пользователя с id = {}.", id);
        return userService.getUsersFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("GET-Запрос на получение общих друзей пользователей с id = {} и otherId = {}.", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable long userId) {
        log.info("GET-Запрос на получение пользователя по id = {}.", userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getUserById(userId));
    }

    @GetMapping("/users/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getRecommendations(@PathVariable long id) {
        log.info("GET-запрос на получение рекомендованных к просмотру фильмов.");
        return userService.getRecommendations(id);
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        log.info("POST-Запрос на добавление пользователя.");
        userService.add(user);
        return user;
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        log.info("PUT-Запрос на обновление пользователя.");
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.update(user));
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("PUT-Запрос на добавление в друзья пользователя friendId = {} от пользователя id = {}.", friendId, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.addFriend(id, friendId));
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("DELETE-Удаление пользователя friendId = {} из друзей пользователя id = {}.", friendId, id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.deleteFriend(id, friendId));
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable long userId) {
        log.info("DELETE-Запрос на удаление пользователя и связанных с ним данных.");
        userService.deleteUser(userId);
    }

    @GetMapping("/users/{id}/feed")
    public List<Event> getFeed(@PathVariable long userId) {
        log.info("GET-Запрос на получение ленты событий пользователя с id = {}.", userId);
        //toDo
        return userService.getFeed(userId);
    }
}