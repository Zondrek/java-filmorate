package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.group.ValidationGroup;

import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @PostMapping
    @Validated(ValidationGroup.OnCreate.class)
    public User createUser(@Valid @RequestBody User user) {
        return service.createUser(user);
    }

    @PutMapping
    @Validated(ValidationGroup.OnUpdate.class)
    public User updateUser(@Valid @RequestBody User user) {
        return service.updateUser(user);
    }

    @GetMapping
    public Collection<User> getUsers() {
        return service.getUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable @NotNull Long id, @PathVariable @NotNull Long friendId) {
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable @NotNull Long id, @PathVariable @NotNull Long friendId) {
        service.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable @NotNull Long id) {
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable @NotNull Long id, @PathVariable @NotNull Long otherId) {
        return service.getCommonFriends(id, otherId);
    }
}
