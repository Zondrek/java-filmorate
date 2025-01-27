package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
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
        return users.get(userId);
    }

    @Override
    public boolean contains(long userId) {
        return users.containsKey(userId);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        users.get(userId).getFriendIds().add(friendId);
        users.get(friendId).getFriendIds().add(userId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        users.get(userId).getFriendIds().remove(friendId);
        users.get(friendId).getFriendIds().remove(userId);
    }

    @Override
    public Set<User> getFriends(long userId) {
        return users.get(userId).getFriendIds()
                .stream()
                .map(users::get)
                .collect(Collectors.toSet());
    }
}
