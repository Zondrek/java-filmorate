package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    private final Map<Long, Set<Long>> friendLinks = new HashMap<>();

    @Override
    public void upsert(User user) {
        users.put(user.getId(), user);
        friendLinks.putIfAbsent(user.getId(), new HashSet<>());
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
        Set<Long> firstFriends = friendLinks.get(userId);
        firstFriends.add(friendId);

        Set<Long> secondFriends = friendLinks.get(friendId);
        secondFriends.add(userId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        Set<Long> firstFriends = friendLinks.get(userId);
        firstFriends.remove(friendId);

        Set<Long> secondFriends = friendLinks.get(friendId);
        secondFriends.remove(userId);
    }

    @Override
    public Set<User> getFriends(long userId) {
        return friendLinks.get(userId)
                .stream()
                .map(users::get)
                .collect(Collectors.toSet());
    }
}
