package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage storage;

    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User createUser(User user) {
        Long id = createId();
        user.setId(id);
        updateName(user);
        storage.upsert(user);
        return user;
    }

    public User updateUser(User newUser) {
        User oldUser = storage.getUser(newUser.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователя с таким идентификатором не существует");
        }
        User result = update(oldUser, newUser);
        storage.upsert(result);
        return result;
    }

    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public void addFriend(long userId, long friendId) {
        checkContainsUsers(userId, friendId);
        storage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        checkContainsUsers(userId, friendId);
        storage.removeFriend(userId, friendId);
    }

    public Set<User> getFriends(long userId) {
        checkContainsUsers(userId);
        return storage.getFriends(userId);
    }

    public Set<User> getCommonFriends(long id, long otherId) {
        checkContainsUsers(id, otherId);
        Set<User> firstFriends = storage.getFriends(id);
        Set<User> secondFriends = storage.getFriends(otherId);
        Set<User> result = new HashSet<>();
        for (User user : firstFriends) {
            if (secondFriends.contains(user)) {
                result.add(user);
            }
        }
        return result;
    }

    private void checkContainsUsers(long... userIds) {
        for (long id : userIds) {
            if (!storage.contains(id)) {
                throw new NotFoundException("Пользователя с таким идентификатором не существует");
            }
        }
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
                .build();
    }
}
