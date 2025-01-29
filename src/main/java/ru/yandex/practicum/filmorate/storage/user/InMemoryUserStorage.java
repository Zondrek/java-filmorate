package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public void upsert(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователя с таким идентификатором не существует");
        }
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        getUser(userId).getFriendIds().add(friendId);
        getUser(friendId).getFriendIds().add(userId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        getUser(userId).getFriendIds().remove(friendId);
        getUser(friendId).getFriendIds().remove(userId);
    }

    @Override
    public Set<User> getFriends(long userId) {
        return getUser(userId).getFriendIds()
                .stream()
                .map(users::get)
                .collect(Collectors.toSet());
    }
}
