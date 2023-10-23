package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserStorage {
    User save(User user);

    User update(User user);

    List<User> getUsers();

    boolean hasKeyInStorage(Long id);

    User getUserFromStorageById(Long id);
}
