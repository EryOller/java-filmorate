package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    public void putLike(Film film, User user) {
        film.getListLikesByUsers().add(user.getId());
    }
    public void removeLike(Film film, User user) {
        film.getLikesByUsers().remove(user.getId());
    }

    public List<Film> getTopPopularFilm(int top) {
        return  InMemoryFilmStorage.getStorage().values().stream()
                .sorted((f1, f2) -> f2.getListLikesByUsers().size() - f1.getListLikesByUsers().size())
                .limit(top)
                .collect(Collectors.toList());
    }
}
