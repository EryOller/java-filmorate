package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.Friendship;
import ru.yandex.practicum.filmorate.model.user.StatusFrindship;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(NamedParameterJdbcOperations namedParameterJdbcOperations, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        if (user.getName() == null || "".equals(user.getName())) {
            user.setName(user.getLogin());
        }
        final String sqlUser = "INSERT INTO users (login, name, email, birthday) " +
                "VALUES (:login, :name, :email, :birthday);";
        final String sqlFriendshipId = "SELECT friendship_id FROM friendship WHERE status = :status;";
        final String sqlSetFriends = "INSERT INTO friends (user_id, friend_id, friendship_id) " +
                "VALUES (:userId, :friendId, :friendshipId);";
        SqlRowSet friendsStatusRows;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("login", user.getLogin());
        map.addValue("name", user.getName());
        map.addValue("email", user.getEmail());
        map.addValue("birthday", user.getBirthday());
        namedParameterJdbcOperations.update(sqlUser, map, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        for (Friendship friendship : user.getFriends()) {
            friendsStatusRows = namedParameterJdbcOperations.queryForRowSet(sqlFriendshipId,
                    Map.of("status", friendship.getStatus().getStatus()));
            friendsStatusRows.next();
            namedParameterJdbcOperations.update(sqlSetFriends, Map.of("userId", user.getId(),
                    "friendId", friendship.getUser(),
                    "friendshipId", friendsStatusRows.getString("FRIENDSHIP_ID")));
        }
        return user;
    }

    @Override
    public User update(User user) {
        final String sqlUpdateUserFieldLogin = "UPDATE users SET login = :login " +
                "WHERE user_id = :userId;";
        final String sqlUpdateUserFieldName = "UPDATE users SET name = :name " +
                "WHERE user_id = :userId;";
        final String sqlUpdateUserFieldEmail = "UPDATE users SET email = :email " +
                "WHERE user_id = :userId;";
        final String sqlUpdateUserFieldBirthday = "UPDATE users SET birthday = :birthday " +
                "WHERE user_id = :userId;";
        final String sqlDeleteFriendsByUserId = "DELETE FROM friends WHERE user_id = :userId;";
        namedParameterJdbcOperations.update(sqlDeleteFriendsByUserId, Map.of("userId", user.getId()));
        Map<Integer, StatusFrindship> statusFriendships = new HashMap();
// получение списка статусов
        final String sqlGetFriendship = "SELECT friendship_id, status FROM friendship;";
        SqlRowSet statusFriendshipRows = jdbcTemplate.queryForRowSet(sqlGetFriendship);
        while (statusFriendshipRows.next()) {
            statusFriendships.put(statusFriendshipRows.getInt("FRIENDSHIP_ID"),
                    Arrays.stream(StatusFrindship.values())
                    .filter(s -> s.getStatus().equals(statusFriendshipRows.getString("STATUS")))
                    .findFirst()
                    .get())  ;
        }

        String sqlGetFriendsFirstQuery = "INSERT INTO friends (user_id, friend_id, friendship_id) VALUES";
        String sqlGetFrindsSumValues = "";
        int countValues = user.getListFriends().size();
        int countIteration = 1;
        // формирование большого инсерта и формирование мапы
        while (countIteration <= countValues) {
            sqlGetFrindsSumValues = sqlGetFrindsSumValues +
                    " (:userId, :friendId" + countIteration + ", :friendshipId" + countIteration +")";

            countIteration++;
            if (countIteration <= countValues) {
                sqlGetFrindsSumValues = sqlGetFrindsSumValues + " ,";
            } else {
                sqlGetFrindsSumValues = sqlGetFrindsSumValues + ";";
            }

        }
        final String sqlInsertFullFriendsByUser = sqlGetFriendsFirstQuery + sqlGetFrindsSumValues;
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("userId", user.getId());
        int count = 1;
        for (Friendship friend : user.getListFriends()) {
            map.addValue("friendId" + count, friend.getUser());
            map.addValue("friendshipId" + count, statusFriendships.keySet()
                    .stream().filter(s -> statusFriendships.get(s).getStatus().equals(friend.getStatus().getStatus()))
                    .findFirst()
                    .get());
            count++;
        }
        if (user.getListFriends().size() != 0) {
            namedParameterJdbcOperations.update(sqlInsertFullFriendsByUser, map);
        }
        namedParameterJdbcOperations.update(sqlUpdateUserFieldLogin,
                Map.of("login", user.getLogin(), "userId", user.getId()));
        namedParameterJdbcOperations.update(sqlUpdateUserFieldName,
                Map.of("name", user.getName(), "userId", user.getId()));
        namedParameterJdbcOperations.update(sqlUpdateUserFieldEmail,
                Map.of("email", user.getEmail(), "userId", user.getId()));
        namedParameterJdbcOperations.update(sqlUpdateUserFieldBirthday, Map.of("birthday", user.getBirthday(),
                "userId", user.getId()));
        return user;
    }

    @Override
    public List<User> getUsers() {
        List<User> users;
        List<Friendship> usersAndFriends = new ArrayList<>();
        final String sqlGetUsers = "SELECT user_id, login, name, email, birthday FROM users";
        final String sqlGetFriendsAllUsers = "SELECT f.user_id, f.friend_id, fs.status " +
                "FROM friends AS f LEFT OUTER JOIN friendship AS fs ON f.friendship_id = fs.friendship_id;";
        SqlRowSet friendsAllUsersRows = jdbcTemplate.queryForRowSet(sqlGetFriendsAllUsers);
        SqlRowSet usersRows = jdbcTemplate.queryForRowSet(sqlGetUsers);
        users = makeListUsers(usersRows);
        while (friendsAllUsersRows.next()) {
            usersAndFriends.add(new Friendship(friendsAllUsersRows.getLong("FRIEND_ID"),
                            Arrays.stream(StatusFrindship.values())
                                    .filter(s -> s.getStatus().equals(friendsAllUsersRows.getString("STATUS")))
                                    .findFirst()
                                    .get()));
        }

        for (User user : users) {
            makeFriends(usersAndFriends, user);
        }
        return users;
    }

    @Override
    public boolean hasKeyInStorage(Long id) {
        final String sqlGetUserById = "SELECT user_id FROM users WHERE user_id = :userId";
        SqlRowSet hasUser = namedParameterJdbcOperations.queryForRowSet(sqlGetUserById, Map.of("userId", id));
        return hasUser.next();
    }

    @Override
    public User getUserFromStorageById(Long id) {
        User user = null;
        List<Friendship> usersAndFriends = new ArrayList<>();
        final String sqlGetUserById = "SELECT user_id, login, name, email, birthday " +
                "FROM users WHERE user_id = :userId;";
        final String sqlGetFriendsByUserId = "SELECT f.friend_id, fs.status FROM friends AS f " +
                "LEFT OUTER JOIN friendship AS fs ON f.friendship_id = fs.friendship_id " +
                "WHERE user_id = :userId;";
        final String sqlGetFriendsAllUsers = "SELECT f.user_id, f.friend_id, fs.status " +
                "FROM friends AS f LEFT OUTER JOIN friendship AS fs ON f.friendship_id = fs.friendship_id;";
        SqlRowSet friendsAllUsersRows = jdbcTemplate.queryForRowSet(sqlGetFriendsAllUsers);
        SqlRowSet userRows = namedParameterJdbcOperations.queryForRowSet(sqlGetUserById, Map.of("userId", id));
        List<User> users = makeListUsers(userRows);
        user = users.get(users.size() - 1);
        while (friendsAllUsersRows.next()) {
            if (id == friendsAllUsersRows.getLong("USER_ID")) {
                usersAndFriends.add(new Friendship(friendsAllUsersRows.getLong("FRIEND_ID"),
                                Arrays.stream(StatusFrindship.values())
                                        .filter(s -> s.getStatus().equals(friendsAllUsersRows.getString("STATUS")))
                                        .findFirst()
                                        .get()));
            }

        }
        user.setFriends(makeFriends(usersAndFriends, user));
        return user;
    }

    private static List<User> makeListUsers(SqlRowSet userResultSet) {
        User user;
        List<User> users = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
        while (userResultSet.next()) {
            user = new User(userResultSet.getString("EMAIL"), userResultSet.getString("LOGIN"),
                    LocalDate.parse(userResultSet.getString("BIRTHDAY"), formatter));
            user.setId(userResultSet.getLong("USER_ID"));
            user.setName(userResultSet.getString("NAME"));
            users.add(user);
        }
        return users;
    }

    private static Set<Friendship> makeFriends(List<Friendship> usersAndFriends, User user) {
        Set<Friendship> friends = new HashSet<>();
        for (Friendship friend : usersAndFriends) {
            friends.add(friend);
        }
        user.setFriends(friends);
        return friends;
    }
}
