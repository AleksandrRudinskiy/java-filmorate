package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Genre {
    private int id;
    private String name;

    public Genre(int id) {
        this.id = id;
    }
}
