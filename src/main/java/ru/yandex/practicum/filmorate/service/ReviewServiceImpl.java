package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDao reviewDao;
    private final FilmStorage filmDao;
    private final UserStorage userDao;

    @Override
    public List<Review> getAll(long filmId, long count) {
        if (filmId != 0) {
            filmDao.getFilmById(filmId);
        }
        return reviewDao.getAll(filmId, count);
    }

    @Override
    public Review get(long id) {
        reviewDao.checkExists(id);
        return reviewDao.get(id);
    }

    @Override
    public Review create(Review review) {
        userDao.checkExists(review.getUserId());
        filmDao.checkExists(review.getFilmId());
        return reviewDao.create(review);
    }

    @Override
    public Review update(Review review) {
        if (review.getReviewId() == null) {
            throw new ValidationException("id обновляемого отзыва не был передан");
        }
        reviewDao.checkExists(review.getReviewId());
        userDao.checkExists(review.getUserId());
        filmDao.checkExists(review.getFilmId());
        return reviewDao.update(review);
    }

    @Override
    public void delete(long id) {
        reviewDao.checkExists(id);
        reviewDao.delete(id);
    }

    @Override
    public void addLike(long id, long userId) {
        reviewDao.checkExists(id);
        userDao.checkExists(userId);
        reviewDao.deleteDislike(id, userId);
        reviewDao.addLike(id, userId);
    }

    @Override
    public void addDislike(long id, long userId) {
        reviewDao.checkExists(id);
        userDao.checkExists(userId);
        reviewDao.deleteLike(id, userId);
        reviewDao.addDislike(id, userId);
    }

    @Override
    public void deleteLike(long id, long userId) {
        reviewDao.checkExists(id);
        userDao.checkExists(userId);
        reviewDao.deleteLike(id, userId);
    }

    @Override
    public void deleteDislike(long id, long userId) {
        reviewDao.checkExists(id);
        userDao.checkExists(userId);
        reviewDao.deleteDislike(id, userId);
    }
}
