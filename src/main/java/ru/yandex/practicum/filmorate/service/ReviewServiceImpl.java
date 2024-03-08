package ru.yandex.practicum.filmorate.service;

import lombok.*;
import org.springframework.stereotype.*;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.*;
import ru.yandex.practicum.filmorate.storage.user.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDao reviewDao;
    private final FilmStorage filmDao;
    private final UserStorage userDao;

    @Override
    public List<Review> getAll(Optional<Long> filmId, long count) {
        filmId.ifPresent(filmDao::getFilmById);

        return reviewDao.getAll(filmId, count);
    }

    @Override
    public Review get(long id) {
        return reviewDao.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв c id = " + id + " не найден"));
    }

    @Override
    public Review create(Review review) {
        userDao.getUserById(review.getUserId());
        filmDao.getFilmById(review.getFilmId());

        return reviewDao.create(review);
    }

    @Override
    public Review update(Review review) {
        if (review.getReviewId() == null) {
            throw new ValidationException("id обновляемого отзыва не был передан");
        }
        reviewDao.get(review.getReviewId())
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + review.getReviewId() + " не найден"));
        userDao.getUserById(review.getUserId());
        filmDao.getFilmById(review.getFilmId());

        return reviewDao.update(review);
    }

    @Override
    public void delete(long id) {
        reviewDao.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id = " + id + " не найден"));

        reviewDao.delete(id);
    }

    @Override
    public void addLike(long id, long userId) {
        reviewDao.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв c id = " + id + " не найден"));
        userDao.getUserById(userId);

        reviewDao.deleteDislike(id, userId);
        reviewDao.addLike(id, userId);
    }

    @Override
    public void addDislike(long id, long userId) {
        reviewDao.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв c id = " + id + " не найден"));
        userDao.getUserById(userId);

        reviewDao.deleteLike(id, userId);
        reviewDao.addDislike(id, userId);
    }

    @Override
    public void deleteLike(long id, long userId) {
        reviewDao.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв c id = " + id + " не найден"));
        userDao.getUserById(userId);

        reviewDao.deleteLike(id, userId);
    }

    @Override
    public void deleteDislike(long id, long userId) {
        reviewDao.get(id)
                .orElseThrow(() -> new NotFoundException("Отзыв c id = " + id + " не найден"));
        userDao.getUserById(userId);

        reviewDao.deleteDislike(id, userId);
    }
}
