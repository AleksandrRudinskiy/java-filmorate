package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import javax.validation.constraints.*;
import java.time.*;
import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private Mpa mpa;
    private List<Genre> genres;
    private Set<Long> likes = new HashSet<>();

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("category_id", mpa.getId());
        return values;
    }
}