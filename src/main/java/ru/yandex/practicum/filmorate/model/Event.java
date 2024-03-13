package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Data

public class Event {
    @EqualsAndHashCode.Exclude
    private long timestamp;
    private long userId;
    private EventType eventType;
    private Operation operation;
    @EqualsAndHashCode.Exclude
    private long eventId;
    private long entityId;

    public Event(long timestamp, long userId, EventType eventType, Operation operation, long entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("time_stamp", new Timestamp(timestamp));
        values.put("user_id", userId);
        values.put("event_type", eventType);
        values.put("operation", operation);
        values.put("entity_id", entityId);
        return values;

    }

}
