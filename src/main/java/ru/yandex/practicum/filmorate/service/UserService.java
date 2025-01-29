package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage storage;

    public User createUser(User user) {
        Long id = createId();
        user.setId(id);
        user.setFriendIds(new HashSet<>());
        updateName(user);
        storage.upsert(user);
        return user;
    }

    public User updateUser(User newUser) {
        User oldUser = storage.getUser(newUser.getId());
        User result = update(oldUser, newUser);
        storage.upsert(result);
        return result;
    }

    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public void addFriend(long userId, long friendId) {
        storage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        storage.removeFriend(userId, friendId);
    }

    public Set<User> getFriends(long userId) {
        return storage.getFriends(userId);
    }

    public Set<User> getCommonFriends(long id, long otherId) {
        Set<User> result = storage.getFriends(id);
        result.retainAll(storage.getFriends(otherId));
        return result;
    }

    private long createId() {
        long currentMaxId = storage.getUsers()
                .stream()
                .map(User::getId)
                .max(Long::compareTo)
                .orElse(0L);
        return ++currentMaxId;
    }

    private void updateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private User update(User originalUser, User newUser) {
        return User.builder()
                .id(newUser.getId())
                .name(newUser.getName() == null ? originalUser.getName() : newUser.getName())
                .login(newUser.getLogin() == null ? originalUser.getLogin() : newUser.getLogin())
                .email(newUser.getEmail() == null ? originalUser.getEmail() : newUser.getEmail())
                .birthday(newUser.getBirthday() == null ? originalUser.getBirthday() : newUser.getBirthday())
                .friendIds(originalUser.getFriendIds())
                .build();
    }
}
