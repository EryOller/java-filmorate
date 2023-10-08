package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Long, Film> storage = new HashMap<>();
    private static Long sequence = 1L;

    @Override
    public Film save(Film film) {
        film.setId(sequence++);
        storage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        storage.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean hasKeyInStorage(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public Film getFilmFromStorageById(Long id) {
        return storage.get(id);
    }
}
