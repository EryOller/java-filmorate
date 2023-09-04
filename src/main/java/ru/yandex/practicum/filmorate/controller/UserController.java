package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private final UserRepository userRepository = new UserRepository();

    @PostMapping(path = "/users")
    public ResponseEntity<User> create(@Valid @RequestBody User newUser) {
        log.debug("Вызов post метода у объекта users");
        if (!userRepository.getUsers().contains(newUser)) {
            log.debug("Сохранение объекта user в репозиторий");
            newUser = userRepository.save(newUser);
            log.info("User сохранен в репозиторий");
        }
        return ResponseEntity.ok(newUser);
    }

    @PutMapping(value = "/users")
    public ResponseEntity<User> update(@Valid @RequestBody User newUser) {
        log.debug("Вызов put метода у объекта users");
        if (userRepository.hasKeyInRepository(newUser.getId())) {
            log.debug("Обновление объекта user в репозитории");
            newUser = userRepository.update(newUser);
            log.info("User обновлен в репозитории");
            return new ResponseEntity<>(newUser, HttpStatus.OK);
        } else {
            log.info("User не обновлен в репозитории");
            log.debug("Не удалось обновить user, так как такой идентификатор не найден");
            return new ResponseEntity<>(newUser, HttpStatus.resolve(500));
        }
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<User>> getUsers() {
        log.debug("Вызов get метода у объекта users");
        List<User> users = userRepository.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
