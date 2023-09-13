package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final static Map<Long, User> storage = new HashMap<>();
    protected static Long sequence = 1L;

    @Override
    public User save(User user) {
        user.setId(sequence++);
        if (user.getName() == null || "".equals(user.getName())) {
            user.setName(user.getLogin());
        }
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean hasKeyInStorage(Long id) {
        return storage.containsKey(id);
    }

    @Override
    public User getUserFromStorageById(Long id) {
        return storage.get(id);
    }

    public static Map<Long, User> getStorage() {
        return storage;
    }
}
