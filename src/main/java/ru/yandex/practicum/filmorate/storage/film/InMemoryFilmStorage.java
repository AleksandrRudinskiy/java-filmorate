package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
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
    public Film deleteLike(Film film, long userId) {
        film.getLikes().remove(userId);
        film.setLikes(film.getLikes());
        return film;
    }

    @Override
    public Film addLike(Film film, long userId) {
        Set<Long> updateLikes = new HashSet<>(film.getLikes());
        updateLikes.add(userId);
        film.setLikes(updateLikes);
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        Optional<Film> film = films.values().stream()
                .filter(item -> item.getId() == id)
                .findFirst();
        return film.orElse(null);
    }

}
