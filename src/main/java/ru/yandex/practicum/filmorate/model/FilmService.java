package ru.yandex.practicum.filmorate.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private int currentId = 1;

    public Film add(Film film) {
        if (film.getId() == 0) {
            film.setId(currentId);
        }
        films.put(film.getId(), film);
        currentId++;
        return film;
    }

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public boolean isAlreadyExists(Film film) {
        return films.containsKey(film.getId());
    }

    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        return film;
    }
}
