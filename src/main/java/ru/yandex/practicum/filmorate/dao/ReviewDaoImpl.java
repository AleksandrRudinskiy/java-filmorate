package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewDaoImpl implements ReviewDao {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final JdbcTemplate jdbcTemplate;
    private final EventDao eventDaoImpl;

    @Override
    public List<Review> getAll(long filmId, long count) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = "SELECT r.review_id, " +
                "r.content, " +
                "r.is_positive, " +
                "r.user_id, " +
                "r.film_id, " +
                "(COUNT(*) FILTER (WHERE rl.is_like) - COUNT(*) FILTER (WHERE NOT rl.is_like)) AS useful " +
                "FROM review AS r " +
                "LEFT JOIN review_likes AS rl ON rl.review_id = r.review_id " +
                "GROUP by (r.review_id) ";
        if (filmId != 0) {
            sql += "HAVING r.film_id = :film_id ";
            parameterSource.addValue("film_id", filmId);
        }
        sql += "ORDER BY useful DESC " +
                "LIMIT :count";
        parameterSource.addValue("count", count);
        return jdbcOperations.query(sql, parameterSource, this::makeReview);
    }

    @Override
    public Review get(long id) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("review_id", id);
        String sql = "SELECT r.review_id, " +
                "r.content, " +
                "r.is_positive, " +
                "r.user_id, " +
                "r.film_id, " +
                "(COUNT(*) FILTER (WHERE rl.is_like) - COUNT(*) FILTER (WHERE NOT rl.is_like)) AS useful " +
                "FROM review AS r " +
                "LEFT JOIN review_likes AS rl ON rl.review_id = r.review_id " +
                "GROUP by (r.review_id) " +
                "HAVING r.film_id = :film_id ";
        parameterSource.addValue("film_id", id);
        Optional<Review> review = jdbcOperations.query(sql, parameterSource, this::makeReview).stream().findFirst();
        if (review.isPresent()) {
            return review.get();
        } else {
            throw new NotFoundException("Ревью с id = " + id + " не найдено.");
        }
    }

    @Override
    public Review create(Review review) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = "INSERT INTO review (content, is_positive, user_id, film_id) " +
                "VALUES (:content, :is_positive, :user_id, :film_id)";
        parameterSource.addValue("content", review.getContent());
        parameterSource.addValue("is_positive", review.getIsPositive());
        parameterSource.addValue("user_id", review.getUserId());
        parameterSource.addValue("film_id", review.getFilmId());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(sql, parameterSource, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Event event = new Event(timestamp.getTime(), review.getUserId(), EventType.REVIEW, Operation.ADD,
                review.getReviewId()
        );
        eventDaoImpl.add(event);
        return review;
    }

    @Override
    public Review update(Review review) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = "UPDATE review " +
                "SET content = :content, " +
                "is_positive = :is_positive " +
                "WHERE review_id = :review_id";
        parameterSource.addValue("review_id", review.getReviewId());
        parameterSource.addValue("content", review.getContent());
        parameterSource.addValue("is_positive", review.getIsPositive());
        jdbcOperations.update(sql, parameterSource);
        Review updateReview = get(review.getReviewId());
        eventDaoImpl.add(new Event((new Timestamp(System.currentTimeMillis())).getTime(),
                updateReview.getUserId(),
                EventType.REVIEW,
                Operation.UPDATE,
                updateReview.getReviewId())
        );
        return updateReview;
    }

    @Override
    public void delete(long id) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("review_id", id);
        String sql = "DELETE FROM review WHERE review_id = :review_id";
        eventDaoImpl.add(new Event((new Timestamp(System.currentTimeMillis())).getTime(),
                get(id).getUserId(),
                EventType.REVIEW,
                Operation.REMOVE,
                get(id).getReviewId())
        );
        jdbcOperations.update(sql, parameterSource);
    }

    @Override
    public void addLike(long id, long userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = "INSERT INTO review_likes " +
                "VALUES (:review_id, :user_id, :like)";
        parameterSource.addValue("review_id", id);
        parameterSource.addValue("user_id", userId);
        parameterSource.addValue("like", true);
        jdbcOperations.update(sql, parameterSource);
    }

    @Override
    public void addDislike(long id, long userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = "INSERT INTO review_likes " +
                "VALUES (:review_id, :user_id, :dislike)";
        parameterSource.addValue("review_id", id);
        parameterSource.addValue("user_id", userId);
        parameterSource.addValue("dislike", false);
        jdbcOperations.update(sql, parameterSource);
    }

    @Override
    public void deleteLike(long id, long userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = "DELETE FROM review_likes " +
                "WHERE review_id = :review_id AND " +
                "user_id = :user_id AND " +
                "is_like = :like";
        parameterSource.addValue("review_id", id);
        parameterSource.addValue("user_id", userId);
        parameterSource.addValue("like", true);
        jdbcOperations.update(sql, parameterSource);
    }

    @Override
    public void deleteDislike(long id, long userId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        String sql = "DELETE FROM review_likes " +
                "WHERE review_id = :review_id AND " +
                "user_id = :user_id AND " +
                "is_like = :dislike";
        parameterSource.addValue("review_id", id);
        parameterSource.addValue("user_id", userId);
        parameterSource.addValue("dislike", false);
        jdbcOperations.update(sql, parameterSource);
    }

    @Override
    public void checkExists(long id) {
        String sql = "SELECT review_id FROM review WHERE review_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        long result = 0;
        if (userRows.next()) {
            result = userRows.getLong("review_id");
        }
        if (result == 0) {
            throw new NotFoundException("Отзыва с id = " + id + " не найдено.");
        }
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return new Review(
                rs.getLong("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getLong("user_id"),
                rs.getLong("film_id"),
                rs.getInt(6)
        );
    }
}