package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Getter
public class FilmService {
    FilmStorage filmStorage;

    @Autowired
    public FilmService() {
        filmStorage = new InMemoryFilmStorage();
    }

    public Film addLike(Film film, long userId) {
        Set<Long> updateLikes = new HashSet<>(film.getLikes());
        updateLikes.add(userId);
        film.setLikes(updateLikes);
        return film;
    }

    public Film deleteLike(Film film, long userId) {
        Set<Long> updateLikes = new HashSet<>(film.getLikes());
        updateLikes.remove(userId);
        film.setLikes(updateLikes);
        return film;
    }

    public List<Film> getBestFilms() {
        return filmStorage.getFilms().stream()
                .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                .limit(10).collect(Collectors.toList());
    }
}
