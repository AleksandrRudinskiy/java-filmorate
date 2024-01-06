package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank
    @NotEmpty
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    private LocalDate releaseDate;
    private int duration;
}
