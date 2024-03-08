package ru.yandex.practicum.filmorate.controller;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.*;

import javax.validation.*;
import java.util.*;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @GetMapping
    public List<Review> getAll(@RequestParam final Optional<Long> filmId,
                               @RequestParam(defaultValue = "10") final long count) {
        return service.getAll(filmId, count);
    }

    @GetMapping("/{id}")
    public Review get(@PathVariable final long id) {
        return service.get(id);
    }

    @PostMapping
    public Review create(@Valid @RequestBody final Review review) {
        return service.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody final Review review) {
        return service.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable final long id) {
        service.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable final long id, @PathVariable final long userId) {
        service.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable final long id, @PathVariable final long userId) {
        service.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable final long id, @PathVariable final long userId) {
        service.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable final long id, @PathVariable final Long userId) {
        service.deleteLike(id, userId);
    }
}