package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDao directorDao;

    @Transactional
    public Director add(Director director) {
        if (director == null) {
            throw new ValidationException("Director cannot be null");
        }
        director.setId(directorDao.add(director));
        return director;
    }

    @Transactional
    public List<Director> executeAddDirectorListToFilm(long filmId, List<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> directorIds = directors.stream()
                .map(Director::getId)
                .collect(Collectors.toList());

        directorDao.addFilmDirectorsBatch(filmId, directorIds);

        // Возвращаем обновленный список режиссеров для фильма
        return getFilmDirectors(filmId);
    }

    @Transactional
    private Director executeAddDirectorToFilm(long filmId, long directorId) {
        return Optional.of(directorDao.findById(directorId)).map(director -> {
            directorDao.addFilmDirector(filmId, directorId);
            return director;
        }).orElseThrow(() -> {
            log.error("Director with id {} does not exist", directorId);
            return new NotFoundException("Не удалось добавить режиссера для фильма");
        });
    }

    public List<Director> getDirectors() {
        return new ArrayList<>(directorDao.findAll());
    }

    public Director getDirectorById(long id) {
        directorDao.checkExists(id);
        return directorDao.findById(id);
    }

    @Transactional
    public Director update(Director director) {
        if (director == null) {
            throw new ValidationException("Director cannot be null");
        }
        directorDao.updateDirector(director);
        return getDirectorById(director.getId());
    }

    @Transactional
    public void deleteDirector(long id) {
        directorDao.checkExists(id);
        directorDao.deleteDirector(id);
    }

    public void deleteFilmDirector(long filmId, long directorId) {
        directorDao.deleteFilmDirector(filmId, directorId);
    }

    public List<Director> getFilmDirectors(long filmId) {
        return directorDao.getFilmDirectors(filmId);
    }
}
