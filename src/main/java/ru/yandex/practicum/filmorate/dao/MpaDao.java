package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.*;

import java.util.*;

public interface MpaDao {
    Mpa getMpaById(int id);

    List<Mpa> getMpa();
}