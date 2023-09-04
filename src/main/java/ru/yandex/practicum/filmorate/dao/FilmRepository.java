package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmRepository {
    protected Map<Integer, Film> films = new HashMap<>();
    private static int sequence = 1;


    public Film save(Film film) {
        film.setId(sequence++);
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public boolean hasKeyInRepository(int id) {
        return films.containsKey(id);
    }
}
