package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, UserStorage userStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmService = filmService;
    }

    @PostMapping(path = "/films")
    public ResponseEntity<Film> create(@Valid @RequestBody Film newFilm) {
        log.debug("Вызов post метода у объекта films");
        if (!filmStorage.getFilms().contains(newFilm)) {
            log.debug("Сохранение объекта film в репозиторий");
            newFilm = filmStorage.save(newFilm);
            log.info("Film сохранен в репозиторий");
        }
        return ResponseEntity.ok(newFilm);
    }

    @PutMapping(value = "/films")
    public ResponseEntity<Film> update(@Valid @RequestBody Film newFilm) {
        log.debug("Вызов put метода у объекта films");
        if (filmStorage.hasKeyInStorage(newFilm.getId())) {
            log.debug("Обновление объекта film в репозитории");
            newFilm = filmStorage.update(newFilm);
            log.info("Film обновлен в репозитории");
            return new ResponseEntity<>(newFilm, HttpStatus.OK);
        } else {
            log.info("Film не обновлен в репозитории");
            log.debug("Не удалось обновить film, так как такой идентификатор не найден");
            return new ResponseEntity<>(newFilm, HttpStatus.resolve(500));
        }
    }

    @GetMapping(value = "/films")
    public ResponseEntity<List<Film>> getAllFilms() {
        log.debug("Вызов get метода у объекта films");
        List<Film> films = filmStorage.getFilms();
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping(value = "/films/{id}")
    public ResponseEntity<Film> getUsers(@PathVariable String id) {
        log.debug("Вызов get метода для получения пользователя по идентификатору");
        Film film = filmStorage.getFilmFromStorageById(Long.valueOf(id));
        if (film != null) {
            return new ResponseEntity<>(film, HttpStatus.OK);
        } else {
            throw new EntityNotFoundException("Фильм с идентификатором " + id + " не найден");
        }
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void putLikeForFilm(@PathVariable Map<String, String> pathVarsMap) {
        Film film = filmStorage.getFilmFromStorageById(Long.valueOf(pathVarsMap.get("id")));
        User user = userStorage.getUserFromStorageById(Long.valueOf(pathVarsMap.get("userId")));
        log.debug("Проверка наличия пользователей в storage");
        if ((film != null) && (user != null)) {
            filmService.putLike(film, user);
            log.debug("Поставил лайк фильму " + film + ". Теперь у фильма " + film.getListLikesByUsers().size());
            log.info("Фильму с id " + pathVarsMap.get("id") + " поставил лайк полькозатель с id "
                    + pathVarsMap.get("userId"));
        } else {
            log.debug("Фильм с id " + pathVarsMap.get("id") + " или  пользователь" + pathVarsMap.get("userId")
                    + " не найден");
            throw new EntityNotFoundException("Фильм или пользователь с идентификатором "
                    + pathVarsMap.get("id") + " не найден");
        }
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void removeFromFriend(@PathVariable Map<String, String> pathVarsMap) {
        Film film = filmStorage.getFilmFromStorageById(Long.valueOf(pathVarsMap.get("id")));
        User user = userStorage.getUserFromStorageById(Long.valueOf(pathVarsMap.get("userId")));
        log.debug("Проверка наличия пользователей в storage");
        if ((film != null) && (user != null)) {
            filmService.removeLike(film, user);
            log.info("Фильму с id " + pathVarsMap.get("id") + " поставил лайк полькозатель с id "
                    + pathVarsMap.get("userId"));
        } else {
            log.debug("Фильм с id " + pathVarsMap.get("id") + " или  пользователь"
                    + pathVarsMap.get("userId") + " не найден");
            throw new EntityNotFoundException("Фильм или пользователь с идентификатором "
                    + pathVarsMap.get("id") + " не найден");
        }
    }

    @GetMapping(value = "/films/popular")
    public ResponseEntity<List<Film>> getTopFilms(@RequestParam(defaultValue = "10") String count) {
        log.debug("Вызов get метода для получения пользователя по идентификатору");
        List<Film> listPopularFilms = filmService.getTopPopularFilm(Integer.parseInt(count), filmStorage);
        return new ResponseEntity<>(listPopularFilms, HttpStatus.OK);
    }
}
