package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface ReviewDao {
    List<Review> getAll(final Optional<Long> filmId, final long count);
    Optional<Review> get(final long id);
    Review create(final Review review);
    Review update(final Review review);
    void delete(final long id);
    void addLike(final long id, final long userId);
    void addDislike(final long id, final long userId);
    void deleteLike(final long id, final long userId);
    void deleteDislike(final long id, final long userId);
}
