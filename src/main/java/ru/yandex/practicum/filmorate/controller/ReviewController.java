package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;

    @GetMapping
    public List<Review> getAll(@RequestParam final Optional<Long> filmId,
                               @RequestParam(defaultValue = "10") final long count) {
        log.info("Получние списка отзывов: Фильтрация по id фильма={}, размер={}",
                filmId.orElse(null), count);
        return service.getAll(filmId, count);
    }

    @GetMapping("/{id}")
    public Review get(@PathVariable final long id) {
        log.info("Получение отзыва: id={}", id);
        return service.get(id);
    }

    @PostMapping
    public Review create(@Valid @RequestBody final Review review) {
        Review created = service.create(review);
        log.info("Создание отзыва: Сгенерированный id={}", created.getReviewId());
        return created;
    }

    @PutMapping
    public Review update(@Valid @RequestBody final Review review) {
        log.info("Обновление отзыва: id={}", review.getReviewId());
        return service.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable final long id) {
        log.info("Удаление пользователя: id={}", id);
        service.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable final long id, @PathVariable final long userId) {
        log.info("Пользователь ставит лайк отзыву: reviewId={}, userId={}", id, userId);
        service.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable final long id, @PathVariable final long userId) {
        log.info("Пользователь ставит дизлайк отзыву: reviewId={}, userId={}", id, userId);
        service.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable final long id, @PathVariable final long userId) {
        log.info("Пользователь удаляет лайк отзыву: reviewId={}, userId={}", id, userId);
        service.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable final long id, @PathVariable final Long userId) {
        log.info("Пользователь удаляет дизлайк отзыву: reviewId={}, userId={}", id, userId);
        service.deleteLike(id, userId);
    }
}