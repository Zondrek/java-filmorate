package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.group.ValidationGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> cache = new HashMap<>();

    @PostMapping
    @Validated(ValidationGroup.OnCreate.class)
    public User createUser(@Valid @RequestBody User user) {
        log.info("POST /users {}", user);
        Long id = createId();
        user.setId(id);
        updateName(user);
        cache.put(id, user);
        return user;
    }

    @PutMapping
    @Validated(ValidationGroup.OnUpdate.class)
    public User updateUser(@Valid @RequestBody User user) {
        log.info("PUT /users {}", user);
        Long id = user.getId();
        if (id == null || !cache.containsKey(id)) {
            throw new NotFoundException("Пользователя с таким идентификатором не существует");
        }
        User result = update(cache.get(id), user);
        cache.put(result.getId(), result);
        return result;
    }

    @GetMapping
    public Collection<User> getUsers() {
        log.info("GET /users, users count: {}", cache.size());
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
        return User.builder()
                .id(newUser.getId())
                .name(newUser.getName() == null ? originalUser.getName() : newUser.getName())
                .login(newUser.getLogin() == null ? originalUser.getLogin() : newUser.getLogin())
                .email(newUser.getEmail() == null ? originalUser.getEmail() : newUser.getEmail())
                .birthday(newUser.getBirthday() == null ? originalUser.getBirthday() : newUser.getBirthday())
                .build();
    }
}
