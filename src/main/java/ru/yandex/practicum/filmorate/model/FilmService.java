package ru.yandex.practicum.filmorate.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmService {

@Autowired
    public FilmService() {
    }

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
