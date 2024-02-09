package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@RestController
@Slf4j
@Data
public class MpaController {
    private final MpaDao mpaDao;

    public MpaController(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpa() {
        log.info("Accepted GET request to get a list of films categories (MPA)");
        List<Mpa> mpas = mpaDao.getMpa();
        log.debug("Current number of users: {}", mpas.size());
        return mpas;
    }

    @GetMapping("/mpa/{mpaId}")
    public ResponseEntity<Mpa> getMpaById(@PathVariable int mpaId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(mpaDao.getMpaById(mpaId));
    }

}
