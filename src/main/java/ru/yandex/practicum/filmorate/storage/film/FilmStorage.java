package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Long upsert(Film film);

    List<Film> getFilms();

    Film getFilm(long filmId);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}
