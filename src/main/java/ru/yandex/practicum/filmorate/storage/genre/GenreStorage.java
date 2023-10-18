package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.film.Genre;


import java.util.List;

public interface GenreStorage {

    List<Genre> getGenres();

    boolean hasKeyInStorage(int id);

    Genre getGenreFromStorageById(int id);

}
