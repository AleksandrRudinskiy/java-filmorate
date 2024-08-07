package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorService directorService;
    private final LocalDate startReleaseDate = LocalDate.of(1895, 12, 28);

    public void addLike(long id, long userId) {
        filmStorage.getFilmById(id);
        filmStorage.addLike(id, userId);
    }

    public Set<Long> getLikes(long id) {
        return filmStorage.getLikes(id);
    }

    /**
     * Удаляет лайк пользователя к фильму.
     *
     * @param id     идентификатор фильма, для которого нужно удалить лайк.
     * @param userId идентификатор пользователя, чей лайк нужно удалить.
     * @return Film  возвращает объект фильма, для которого был удален лайк.
     * @throws NotFoundException если пользователь с указанным идентификатором не найден.
     * @throws NotFoundException если фильм с указанным идентификатором не найден.
     */
    public Film deleteLike(long id, long userId) {
        userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(id);
        film.getLikes().remove(userId);
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> getBestFilms(int genreId, int year, int count) {
        return filmStorage.getBestFilms(genreId, year, count);
    }

    public List<Film> getFilms() {
        return filmStorage.getAllFilms();
    }


    /**
     * Возвращает список фильмов, которые понравились обоим пользователям.
     *
     * @param userId   идентификатор первого пользователя.
     * @param friendId идентификатор второго пользователя.
     * @return List<Film> возвращает список фильмов, которые понравились обоим пользователям.
     * @throws NotFoundException если пользователь с указанным идентификатором не найден.
     */
    public List<Film> getCommonFilms(int userId, int friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void add(Film film) {
        validateFilm(film);
        filmStorage.add(film);
        List<Director> filmDirectors = directorService.executeAddDirectorListToFilm(film.getId(), film.getDirectors());
        film.setDirectors(filmDirectors);
    }

    public Film update(Film film) {
        filmStorage.checkExists(film.getId());
        List<Director> filmDirectors = directorService.executeAddDirectorListToFilm(film.getId(), film.getDirectors());
        List<Director> currentFilmDirectors = directorService.getFilmDirectors(film.getId());
        currentFilmDirectors.stream()
                .filter(c -> !filmDirectors.contains(c))
                .forEach(c -> directorService.deleteFilmDirector(film.getId(), c.getId()));
        film.setDirectors(filmDirectors);
        return filmStorage.update(film);
    }

    public List<Film> findAllByDirectorIdSorted(Long directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        return filmStorage.findAllByDirectorIdSorted(directorId, sortBy);
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public void deleteFilm(long filmId) {
        filmStorage.deleteFilm(filmId);
    }


    public List<Film> search(String query, String by) {
        return getFilms().stream()
                .filter(f -> ((by.toLowerCase().contains("title") && f.getDescription().toLowerCase().contains(query.toLowerCase()))
                                || (by.toLowerCase().contains("director") && f.getDirectors().stream().anyMatch(d -> d.getName().toLowerCase().contains(query.toLowerCase())))
                        )
                )
                .sorted((f1, f2) -> getLikes(f2.getId()).size() - getLikes(f1.getId()).size())
                .collect(Collectors.toList());
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
