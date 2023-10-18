package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@RestController
@Slf4j
public class GenreController {
    private final GenreStorage genreStorage;


    @Autowired
    public GenreController(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping(value = "/genres")
    public ResponseEntity<List<Genre>> getGenre() {
        log.debug("Вызов get метода у объекта Genre");
        List<Genre> genres = genreStorage.getGenres();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @GetMapping(value = "/genres/{id}")
    public ResponseEntity<Genre> getGenre(@PathVariable String id) {
        log.debug("Вызов get метода для получения жанра по идентификатору");
        int idGenre = Integer.parseInt(id);
        if (genreStorage.hasKeyInStorage(idGenre)) {
            List<Genre> genres = genreStorage.getGenres();
            Genre genre = genres.get(idGenre - 1);
            return new ResponseEntity<>(genre, HttpStatus.OK);
        } else {
            log.info("Genre не удалось получить из репозитории");
            log.debug("Не удалось получить genre, так как такой идентификатор не найден");
            return new ResponseEntity<>(null, HttpStatus.resolve(404));
        }
    }
}
