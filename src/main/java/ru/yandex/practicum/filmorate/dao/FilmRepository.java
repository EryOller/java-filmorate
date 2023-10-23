package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmRepository {
    protected Map<Long, Film> films = new HashMap<>();
    private static Long sequence = 1L;


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

    public boolean hasKeyInRepository(Long id) {
        return films.containsKey(id);
    }
}
