package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@RestController
@Slf4j
@Data
public class GenreController {


    GenreServise


    @GetMapping("/genres")
    public List<Genre> getGenres() {
        log.info("Accepted GET request to get a list of films genres");
        List<Genre> genres = userService.getUsers();
        log.debug("Current number of users: {}", users.size());
        return users;
    }
}
