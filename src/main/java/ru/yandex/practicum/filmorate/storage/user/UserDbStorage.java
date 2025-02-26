package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.List;
import java.util.Set;

@Primary
@Component
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String CREATE_USER_QUERY = "INSERT INTO user_table (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE user_table " +
            "SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String GET_USERS_QUERY = "SELECT * FROM user_table";
    private static final String GET_USER_QUERY = "SELECT * FROM user_table WHERE id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friend_link_table (user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friend_link_table " +
            "WHERE user_id = ? AND friend_id = ?";
    private static final String GET_FRIENDS_QUERY = "SELECT u.* FROM user_table u " +
            "JOIN friend_link_table f ON u.id = f.friend_id WHERE f.user_id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Long upsert(User user) {
        if (user.getId() == null) {
            Integer userId = insert(CREATE_USER_QUERY,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday());
            return Long.valueOf(userId);
        } else {
            update(UPDATE_USER_QUERY,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            return user.getId();
        }
    }

    @Override
    public List<User> getUsers() {
        return findMany(GET_USERS_QUERY);
    }

    @Override
    public User getUser(long userId) {
        checkUserExistsOrThrow(userId);
        return findOne(GET_USER_QUERY, userId);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        checkUserExistsOrThrow(userId);
        checkUserExistsOrThrow(friendId);
        insert(ADD_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        checkUserExistsOrThrow(userId);
        checkUserExistsOrThrow(friendId);
        remove(REMOVE_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public Set<User> getFriends(long userId) {
        checkUserExistsOrThrow(userId);
        return Set.copyOf(findMany(GET_FRIENDS_QUERY, userId));
    }

    public void checkUserExistsOrThrow(long userId) {
        String sqlQuery = "SELECT COUNT(*) FROM user_table WHERE id = ?";
        int count = jdbc.queryForObject(sqlQuery, Integer.class, userId);
        if (count == 0) {
            throw new NotFoundException("Пользователя с таким идентификатором не существует");
        }
    }
}