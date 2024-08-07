package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getDirectors() {
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable long id) {
        return new ResponseEntity<>(directorService.getDirectorById(id), HttpStatus.OK);
    }

    @PostMapping
    public Director add(@RequestBody @Valid Director director) {
        return directorService.add(director);
    }

    @PutMapping
    public Director update(@RequestBody @Valid Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirectorById(@PathVariable int id) {
        directorService.deleteDirector(id);
        return ResponseEntity.noContent().build();
    }
}
