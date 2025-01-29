package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    void upsert(User user);

    List<User> getUsers();

    User getUser(long userId);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    Set<User> getFriends(long userId);
}
