package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.user.Friendship;
import ru.yandex.practicum.filmorate.model.user.StatusFrindship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DBUserRepositoryTest {
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {
        User user = userStorage.getUserFromStorageById(2L);
        assertNotNull(user);
        assertEquals(2L, user.getId());
        assertEquals("mitay@mail.ru", user.getEmail());
    }

    @Test
    public void testHasUserById() {
        boolean hasKeyInStorage = userStorage.hasKeyInStorage(3L);
        User user = userStorage.getUserFromStorageById(3L);
        assertNotNull(user);
        assertEquals(3L, user.getId());
        assertEquals(true, hasKeyInStorage);
    }

    @Test
    public void testSaveUserAndGetAllUsers() {
        User oleg = new User("medic@mail.ru", "proctolog", LocalDate.of(1991, 02, 01));
        oleg.setName("Олег");
        Friendship friendshipWithOlga = new Friendship(1L, StatusFrindship.CONFIRMED);
        Friendship friendshipWithIvan = new Friendship(2L, StatusFrindship.UNCONFIRMED);
        Set<Friendship> friendsByUser = new HashSet<>();
        friendsByUser.add(friendshipWithOlga);
        friendsByUser.add(friendshipWithIvan);
        oleg.setFriends(friendsByUser);
        int countUsersBeforeAdd = userStorage.getUsers().size();
        userStorage.save(oleg);
        int countUsersAfterAdd = userStorage.getUsers().size();
        assertEquals(countUsersAfterAdd, countUsersBeforeAdd + 1);
        User user = userStorage.getUserFromStorageById(Long.valueOf(countUsersAfterAdd));
        assertNotNull(user);
        assertEquals("medic@mail.ru", user.getEmail());
    }

    @Test
    public void testUpdateUser() {
        User oksana = new User("sberbank@mail.ru", "CreditManager", LocalDate.of(2000, 12, 15));
        oksana.setName("Оксана");
        Friendship friendshipWithOlga = new Friendship(1L, StatusFrindship.CONFIRMED);
        Friendship friendshipWithIvan = new Friendship(2L, StatusFrindship.UNCONFIRMED);
        Set<Friendship> friendsByUser = new HashSet<>();
        friendsByUser.add(friendshipWithOlga);
        friendsByUser.add(friendshipWithIvan);
        oksana.setFriends(friendsByUser);
        userStorage.save(oksana);
        int countUsers = userStorage.getUsers().size();

        oksana.setId(Long.valueOf(countUsers));
        oksana.setEmail("alfa@mail.ru");
        oksana.setLogin("oksana");
        userStorage.update(oksana);
        User user = userStorage.getUserFromStorageById(Long.valueOf(countUsers));

        assertNotNull(user);
        assertEquals("alfa@mail.ru", user.getEmail());
        assertEquals("oksana", user.getLogin());
        assertEquals("Оксана", user.getName());

    }


}
