package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.Friendship;
import ru.yandex.practicum.filmorate.model.user.StatusFrindship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class UserService {

    private static final String CREATE_FRIENDSHIP = "Create friendship";
    private static final String BREAK_OFF_FRIENDSHIP = "Break off friendship";

    public void makeFriends(User initiator, User permissive) {
        if (changeStatusFriendshipPermissive(initiator, permissive, CREATE_FRIENDSHIP)) {
            initiator.getFriends().add(new Friendship(permissive.getId(), StatusFrindship.CONFIRMED));
        } else {
            initiator.getFriends().add(new Friendship(permissive.getId(), StatusFrindship.UNCONFIRMED));
        }
    }

    public void breakOffFriendship(User initiator, User permissive) {
        if (changeStatusFriendshipPermissive(initiator, permissive, BREAK_OFF_FRIENDSHIP)) {
            initiator.getFriends().remove(new Friendship(permissive.getId(), StatusFrindship.CONFIRMED));
        } else {
            initiator.getFriends().remove(new Friendship(permissive.getId(), StatusFrindship.UNCONFIRMED));
        }
    }

    public List<User> getListCommonFriends(User initiator, User permissive, UserStorage storage) {
        List<User> friendsList = new ArrayList<>();
        for (Friendship friend : initiator.getListFriends()) {
            if (StatusFrindship.CONFIRMED.equals(friend.getStatus())) {
                if (permissive.getListFriends().contains(friend)) {
                    storage.getUsers().stream()
                            .filter(u -> u.getId() == friend.getUser())
                            .findFirst()
                            .map(u -> friendsList.add(u));
                }
            }
        }
        return friendsList;
    }

    public List<User> getListFriends(User user, UserStorage storage) {
        List<User> friendsList = new ArrayList<>();
        for (Friendship friend : user.getListFriends() ) {
            if (StatusFrindship.CONFIRMED.equals(friend.getStatus())) {
                storage.getUsers().stream()
                        .filter(u -> u.getId() == friend.getUser())
                        .findFirst()
                        .map(u -> friendsList.add(u));
            }
        }
        return friendsList;
    }

    private boolean changeStatusFriendshipPermissive(User initiator, User permissive, String actionForFriendship) {
        Iterator<Friendship> iterator = permissive.getFriends().iterator();
        while (iterator.hasNext()) {
            Friendship friendship = iterator.next();
            if (friendship.getUser() == initiator.getId()) {
                if (CREATE_FRIENDSHIP.equals(actionForFriendship)) {
                    if (friendship.getStatus().equals(StatusFrindship.UNCONFIRMED)) {
                        friendship.setStatus(StatusFrindship.CONFIRMED);
                    }
                }
                if (BREAK_OFF_FRIENDSHIP.equals(actionForFriendship)) {
                    if (friendship.getStatus().equals(StatusFrindship.CONFIRMED)) {
                        friendship.setStatus(StatusFrindship.UNCONFIRMED);
                    }
                }
                return true;
            }
        }
        return false;
    }
}
