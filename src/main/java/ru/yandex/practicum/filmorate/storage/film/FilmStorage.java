package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.sql.SQLException;
import java.util.List;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    List<Film> getFilms();

    boolean hasKeyInStorage(Long id);

    Film getFilmFromStorageById(Long id) throws SQLException;
}
