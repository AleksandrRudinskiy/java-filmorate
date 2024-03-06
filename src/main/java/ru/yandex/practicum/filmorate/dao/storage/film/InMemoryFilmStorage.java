package ru.yandex.practicum.filmorate.dao.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.dao.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private int currentFilmId = 1;

    @Override
    public Film add(Film film) {
        if (film.getId() == 0 && !films.containsValue(film)) {
            film.setId(currentFilmId);
        }
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        currentFilmId++;
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean isAlreadyExists(long id) {
        return films.containsKey(id);
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
    public Film addLike(long id, long userId) {
        if (!isAlreadyExists(id)) {
            throw new NotFoundException("film с id = " + id + " не найден");
        }
        Film film = getFilmById(id);
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public List<Film> getBestFilms(int count) {
        return films.values().stream()
                .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("film с id = " + id + " не найден");
        }
        return films.get(id);
    }
}
