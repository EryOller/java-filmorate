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
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping(path = "/users")
    public ResponseEntity<User> create(@Valid @RequestBody User newUser) {
        log.debug("Вызов post метода у объекта users");
        if (!userStorage.getUsers().contains(newUser)) {
            log.debug("Сохранение объекта user в репозиторий");
            newUser = userStorage.save(newUser);
            log.info("User сохранен в репозиторий");
        }
        return ResponseEntity.ok(newUser);
    }

    @PutMapping(value = "/users")
    public ResponseEntity<User> update(@Valid @RequestBody User newUser) {
        log.debug("Вызов put метода у объекта users");
        if (userStorage.hasKeyInStorage(newUser.getId())) {
            log.debug("Обновление объекта user в репозитории");
            newUser = userStorage.update(newUser);
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
        List<User> users = userStorage.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Map<String, String> pathVarsMap) {
        User initiator = userStorage.getUserFromStorageById(Long.valueOf(pathVarsMap.get("id")));
        User permissive = userStorage.getUserFromStorageById(Long.valueOf(pathVarsMap.get("friendId")));
        log.debug("Проверка наличия пользователей в storage");
        if ((initiator != null) && (permissive != null)) {
            userService.makeFriends(initiator, permissive);
            log.info("Пользователи подружились");
        } else {
            log.debug("Пользователь с id " + pathVarsMap.get("id") + " или "
                    + pathVarsMap.get("friendId") + " отсутствует");
            throw new EntityNotFoundException("Пользователь с идентификатором " + pathVarsMap.get("id")
                    + " или " + pathVarsMap.get("friendId") + " не найден");
        }
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void removeFromFriend(@PathVariable Map<String, String> pathVarsMap) {
        User initiator = userStorage.getUserFromStorageById(Long.valueOf(pathVarsMap.get("id")));
        User permissive = userStorage.getUserFromStorageById(Long.valueOf(pathVarsMap.get("friendId")));
        log.debug("Проверка наличия пользователей в storage");
        if ((initiator != null) && (permissive != null)) {
            userService.breakOffFriendship(initiator, permissive);
            log.info("Пользователи с id " + pathVarsMap.get("id") + " и " + pathVarsMap.get("friendId") + " больше не друзья");
        } else {
            log.debug("Пользователь с id " + pathVarsMap.get("id") + " или " + pathVarsMap.get("friendId") + " отсутствует");
            throw new EntityNotFoundException("Пользователь с идентификатором " + pathVarsMap.get("id") + " или "
                    + pathVarsMap.get("friendId") + " не найден");
        }
    }

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        log.debug("Вызов get метода для получения пользователя по идентификатору");
        if (userStorage.hasKeyInStorage(Long.valueOf(id))) {
            User user = userStorage.getUserFromStorageById(Long.valueOf(id));
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            throw new EntityNotFoundException("Пользователь с идентификатором " + id + " не найден");
        }
    }

    @GetMapping(value = "/users/{id}/friends")
    public ResponseEntity<List<User>> getListFriendsByUserId(@PathVariable String id) {
        log.debug("Вызов get метода для получения списка друзей");
        if (userStorage.hasKeyInStorage(Long.valueOf(id))) {
            List<User> friends = userService.getListFriends(userStorage.getUserFromStorageById(Long.valueOf(id)),
                    userStorage);
            return new ResponseEntity<>(friends, HttpStatus.OK);
        } else {
            throw new EntityNotFoundException("Пользователь с идентификатором " + id + " не найден");
        }
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getGeneralListOfFriends(@PathVariable Map<String, String> pathVarsMap) {
        User initiator = userStorage.getUserFromStorageById(Long.valueOf(pathVarsMap.get("id")));
        User permissive = userStorage.getUserFromStorageById(Long.valueOf(pathVarsMap.get("otherId")));
        log.debug("Проверка наличия пользователей в storage");
        if ((initiator != null) && (permissive != null)) {
            List<User> listOfCommonFriends = userService.getListCommonFriends(initiator, permissive, userStorage);
            log.info("Пользователи с id " + pathVarsMap.get("id") + " и " + pathVarsMap.get("otherId") + " больше не друзья");
            return new ResponseEntity<>(listOfCommonFriends, HttpStatus.OK);
        } else {
            log.debug("Пользователь с id " + pathVarsMap.get("id") + " или "
                    + pathVarsMap.get("otherId") + " отсутствует");
            throw new EntityNotFoundException("Пользователь с идентификатором " + pathVarsMap.get("id") + " или "
                    + pathVarsMap.get("otherId") + " не найден");
        }
    }
}
