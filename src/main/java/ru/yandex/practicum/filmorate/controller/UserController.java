package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserService;
import ru.yandex.practicum.filmorate.model.ValidationException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

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
        List<User> users = userService.getUsers();
        log.info("Accepted GET request to get a list of all users");
        log.debug("Current number of users: {}", users.size());
        return users;
    }

    @PostMapping(value = "/users")
    public User createUser(@Valid @RequestBody User user) {
        validateUser(user);
        userService.add(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user, HttpServletResponse response) {
        validateUser(user);
        if (userService.isAlreadyExists(user)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return userService.update(user);
        } else if (!userService.isAlreadyExists(user) && (user.getId() != 0)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return user;
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            return userService.add(user);
        }
    }

    private void validateUser(User user) {
        if ((user.getEmail().isEmpty()) || (!user.getEmail().contains("@"))) {
            throw new ValidationException("E-mail is empty or not contains symbol \"@\"");
        }
        if ((user.getLogin().isEmpty()) || user.getLogin().contains(" ")) {
            throw new ValidationException("Login is empty or contains a space");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth cannot be in the future");
        }
    }
}
