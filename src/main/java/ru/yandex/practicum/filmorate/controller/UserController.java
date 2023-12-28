package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.UserConfig;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserService;
import ru.yandex.practicum.filmorate.model.ValidationException;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private final ApplicationContext context = new AnnotationConfigApplicationContext(UserConfig.class);
    private final UserService userService = context.getBean("userService", UserService.class);

    @GetMapping("/users")
    public List<User> findAllUsers() {
        List<User> users = userService.getUsers();
        log.debug("Current number of users: {}", users.size());
        return users;
    }

    @PostMapping(value = "/users")
    public User createUser(@RequestBody User user) {
        validateUser(user);
        userService.add(user);
        return user;
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user, HttpServletResponse response) {
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
