package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Genre {
    private int id;
    private String name;
}
