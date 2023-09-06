package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private final FilmRepository filmRepository = new FilmRepository();

    @PostMapping(path = "/films")
    public ResponseEntity<Film> create(@Valid @RequestBody Film newFilm) {
        log.debug("Вызов post метода у объекта films");
        if (!filmRepository.getFilms().contains(newFilm)) {
            log.debug("Сохранение объекта film в репозиторий");
            newFilm = filmRepository.save(newFilm);
            log.info("Film сохранен в репозиторий");
        }
        return ResponseEntity.ok(newFilm);
    }

    @PutMapping(value = "/films")
    public ResponseEntity<Film> update(@Valid @RequestBody Film newFilm) {
        log.debug("Вызов put метода у объекта films");
        if (filmRepository.hasKeyInRepository(newFilm.getId())) {
            log.debug("Обновление объекта film в репозитории");
            newFilm = filmRepository.update(newFilm);
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
        List<Film> films = filmRepository.getFilms();
        return new ResponseEntity<>(films, HttpStatus.OK);
    }
}
