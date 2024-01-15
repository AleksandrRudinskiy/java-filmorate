package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

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

    @Override
    public List<Film> getBestFilms(int count) {
        return films.values().stream()
                .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film deleteLike(long id, long userId) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("film с id = " + id + " не найден");
        }
        if (userId < 0) {
            throw new NotFoundException("Неверный id пользователя");
        }
        Film film = getFilmById(id);
        film.getLikes().remove(userId);
        return film;
    }

    @Override
    public Film addLike(Long id, long userId) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("film с id = " + id + " не найден");
        }
        Film film = films.get(id);
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public Film getFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("film с id = " + id + " не найден");
        }
        return films.get(id);
    }

}
