package ru.yandex.practicum.filmorate.controller;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

@RestController
@Slf4j
@AllArgsConstructor
public class MpaController {
    private final MpaDao mpaDao;

    @GetMapping("/mpa")
    public List<Mpa> getMpa() {
        log.info("GET-запрос на получение всех категорий (MPA).");
        List<Mpa> mpas = mpaDao.getMpa();
        log.info("Количество категорий (MPA): {}", mpas.size());
        return mpas;
    }

    @GetMapping("/mpa/{mpaId}")
    public ResponseEntity<Mpa> getMpaById(@PathVariable int mpaId) {
        log.info("GET-запрос на получение категории (MPA) по id = {}.", mpaId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(mpaDao.getMpaById(mpaId));
    }
}
