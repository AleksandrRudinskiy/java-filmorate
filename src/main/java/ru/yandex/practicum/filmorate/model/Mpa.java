package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor

public class Mpa {
    private int id;
    private String name;

    public Mpa(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Mpa{" +
                "id=" + id +
                '}';
    }
}
