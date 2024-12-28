package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> cache = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        Long id = user.getId();
        if (user.getId() == null) {
            id = createId();
        } else if (cache.containsKey(id)) {
            throw new ValidationException("Пользователь с таким идентификатором уже существует");
        }
        user.setId(id);
        updateName(user);
        cache.put(id, user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        Long id = user.getId();
        if (id == null || !cache.containsKey(id)) {
            throw new ValidationException("Пользователя с таким идентификатором не существует");
        }
        User result = update(cache.get(id), user);
        cache.put(result.getId(), result);
        return result;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return cache.values();
    }

    private long createId() {
        long currentMaxId = cache.keySet()
                .stream()
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
        updateName(newUser);
        User.UserBuilder builder = User.builder();
        builder.id(newUser.getId());
        builder.name(newUser.getName() == null ? originalUser.getName() : newUser.getName());
        builder.login(newUser.getLogin() == null ? originalUser.getLogin() : newUser.getLogin());
        builder.email(newUser.getEmail() == null ? originalUser.getEmail() : newUser.getEmail());
        builder.birthday(newUser.getBirthday() == null ? originalUser.getBirthday() : newUser.getBirthday());
        return builder.build();
    }
}
