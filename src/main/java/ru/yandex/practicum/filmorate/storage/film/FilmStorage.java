package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLikes;

import java.util.List;

public interface FilmStorage {

    void upsert(Film film);

    List<Film> getFilms();

    Film getFilm(long filmId);

    boolean contains(long filmId);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<FilmLikes> getFilmLikes();
}
