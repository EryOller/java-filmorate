package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.Friendship;
import ru.yandex.practicum.filmorate.model.user.StatusFrindship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service("userDbService")
public class UserService {
    private static final String CREATE_FRIENDSHIP = "Create friendship";
    private static final String BREAK_OFF_FRIENDSHIP = "Break off friendship";
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public void makeFriends(User initiator, User permissive) {
        if (changeStatusFriendshipPermissive(initiator, permissive, CREATE_FRIENDSHIP)) {
            initiator.getFriends().add(new Friendship(permissive.getId(), StatusFrindship.CONFIRMED));
        } else {
            initiator.getFriends().add(new Friendship(permissive.getId(), StatusFrindship.UNCONFIRMED));
        }
        userStorage.update(initiator);
        userStorage.update(permissive);
    }

    public void breakOffFriendship(User initiator, User permissive) {
        if (changeStatusFriendshipPermissive(initiator, permissive, BREAK_OFF_FRIENDSHIP)) {
            initiator.getFriends().remove(new Friendship(permissive.getId(), StatusFrindship.CONFIRMED));
        } else {
            initiator.getFriends().remove(new Friendship(permissive.getId(), StatusFrindship.UNCONFIRMED));
        }
    }

    public List<User> getListCommonFriends(User initiator, User permissive, UserStorage storage) {
        List<User> friends = new ArrayList<>();
        for (Friendship friend : initiator.getListFriends()) {
            if (permissive.getListFriends().contains(friend)) {
                storage.getUsers().stream()
                        .filter(u -> u.getId() == friend.getUser())
                        .findFirst()
                        .map(u -> friends.add(u));
            }
        }
        return friends;
    }

    public List<User> getListFriends(User user, UserStorage storage) {
        List<User> friends = new ArrayList<>();
        for (Friendship friend : user.getListFriends()) {
            storage.getUsers().stream()
                    .filter(u -> u.getId() == friend.getUser())
                    .findFirst()
                    .map(u -> friends.add(u));
        }
        return friends.stream().sorted((u1, u2) -> {
             if (u2.getId() - u1.getId() < 0) {
                 return 1;
             } else if (u2.getId() - u1.getId() > 0) {
                 return -1;
             } else {
                 return 0;
             }
        }
                )
                .collect(Collectors.toList());
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
