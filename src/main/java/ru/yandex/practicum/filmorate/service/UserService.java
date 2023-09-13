package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    public void makeFriends(User initiator, User permissive) {
        initiator.getFriends().add(permissive.getId());
        permissive.getFriends().add(initiator.getId());
    }

    public void breakOffFriendship(User initiator, User permissive) {
        initiator.getListFriends().remove(permissive.getId());
        permissive.getListFriends().remove(initiator.getId());
    }

    public List<User> getListCommonFriends(User initiator, User permissive) {
        List<User> friendsList = new ArrayList<>();
        for (Long friend : initiator.getListFriends()) {
            if (permissive.getListFriends().contains(friend)) {
                friendsList.add(InMemoryUserStorage.getStorage().get(friend));
            }
        }
        return friendsList;
    }

    public List<User> getListFriends(User user) {
        List<User> friendsList = new ArrayList<>();
        for (Long friend : user.getListFriends()) {
            friendsList.add(InMemoryUserStorage.getStorage().get(friend));
        }
        return friendsList;
    }

}
