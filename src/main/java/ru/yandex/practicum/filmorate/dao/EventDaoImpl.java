package ru.yandex.practicum.filmorate.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

@Component
@AllArgsConstructor
public class EventDaoImpl implements EventDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Event event) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("event_id");
        long eventId = simpleJdbcInsert.executeAndReturnKey(event.toMap()).longValue();
        event.setEventId(eventId);
    }

}
