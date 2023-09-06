package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    protected Map<Integer, User> users = new HashMap<>();
    private static int sequence = 1;


    public User save(User user) {
        user.setId(sequence++);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public boolean hasKeyInRepository(int id) {
        return users.containsKey(id);
    }
}
