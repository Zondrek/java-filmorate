package ru.yandex.practicum.filmorate.model;

import java.util.Set;

public record FilmLikes(long filmId, Set<Long> likes) {
}
