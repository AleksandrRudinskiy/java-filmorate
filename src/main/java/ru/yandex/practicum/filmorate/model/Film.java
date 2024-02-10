package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
public class Film {
    @EqualsAndHashCode.Exclude
    private long id;
    @NotBlank
    @NotEmpty
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    private LocalDate releaseDate;
    private int duration;

    private Set<Long> likes;
    private List<Genre> genres;
    private Mpa mpa;




    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", name);
        values.put("description", description);
        values.put("releaseDate", releaseDate);
        values.put("duration", duration);
        return values;
    }
}
