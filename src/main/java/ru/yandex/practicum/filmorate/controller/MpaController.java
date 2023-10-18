package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@RestController
@Slf4j
public class MpaController {
    private final MpaStorage mpaStorage;


    @Autowired
    public MpaController (MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping(value = "/mpa")
    public ResponseEntity<List<Mpa>> getMpa() {
        log.debug("Вызов get метода у объекта Genre");
        List<Mpa> mpa = mpaStorage.getMpa();
        return new ResponseEntity<>(mpa, HttpStatus.OK);
    }

    @GetMapping(value = "/mpa/{id}")
    public ResponseEntity<Mpa> getMpa(@PathVariable String id) {
        log.debug("Вызов get метода для получения рейтинга по идентификатору");
        int idMpa = Integer.parseInt(id);
        List<Mpa> mpas;
        Mpa mpa;
        if (mpaStorage.hasKeyInStorage(idMpa)) {
            mpas = mpaStorage.getMpa();
            mpa = mpas.get(idMpa - 1);
            return new ResponseEntity<>(mpa, HttpStatus.OK);
        } else {
            log.info("Mpa не удалось получить из репозитории");
            log.debug("Не удалось получить mpa, так как такой идентификатор не найден");
            return new ResponseEntity<>(null, HttpStatus.resolve(404));
        }
    }
}
