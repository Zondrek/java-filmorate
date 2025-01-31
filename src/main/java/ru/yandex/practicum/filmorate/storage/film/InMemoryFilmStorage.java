package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public void upsert(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(long filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильма с таким идентификатором не существует");
        }
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        getFilm(filmId).getUserLikes().add(userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        getFilm(filmId).getUserLikes().remove(userId);
    }
}
