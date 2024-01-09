package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private int currentFilmId = 1;
    private final LocalDate startReleaseDate = LocalDate.of(1895, 12, 28);

    @Override
    public Film add(Film film) {
        validateFilm(film);
        if (film.getId() == 0 && !films.containsValue(film)) {
            film.setId(currentFilmId);
        }
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        currentFilmId++;
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean isAlreadyExists(Film film) {
        return films.containsKey(film.getId());
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return film;
    }

    @Override
    public Map<Long, Film> getFilmsMap() {
        return films;
    }

    private void validateFilm(Film film) {
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ValidationException("The name is empty");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("The description length more than 200 symbols");
        }
        if (film.getReleaseDate().isBefore(startReleaseDate)) {
            throw new ValidationException("Release date earlier than 28-12-1895");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Film duration is negative");
        }
    }

}
