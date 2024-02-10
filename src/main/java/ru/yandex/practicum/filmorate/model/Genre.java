package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@ToString
public class Genre {
    private int id;
    private String name;

}
