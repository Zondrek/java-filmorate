package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

public class Utils {

    public static void checkContainsUsers(UserStorage storage, long... userIds) {
        for (long id : userIds) {
            if (!storage.contains(id)) {
                throw new NotFoundException("Пользователя с таким идентификатором не существует");
            }
        }
    }

    public static void checkContainsFilms(FilmStorage storage, long... filmIds) {
        for (long id : filmIds) {
            if (!storage.contains(id)) {
                throw new NotFoundException("Фильма с таким идентификатором не существует");
            }
        }
    }
}
