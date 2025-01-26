package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLikes;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    private final Map<Long, Set<Long>> likes = new HashMap<>(); // <filmId, Set<userId>>

    @Override
    public void upsert(Film film) {
        films.put(film.getId(), film);
        likes.putIfAbsent(film.getId(), new HashSet<>());
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(long filmId) {
        return films.get(filmId);
    }

    @Override
    public boolean contains(long filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public void addLike(long filmId, long userId) {
        Set<Long> userIds = likes.get(filmId);
        userIds.add(userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        Set<Long> userIds = likes.get(filmId);
        userIds.remove(userId);
    }

    @Override
    public List<FilmLikes> getFilmLikes() {
        return likes.entrySet()
                .stream()
                .map(entry -> new FilmLikes(entry.getKey(), entry.getValue()))
                .toList();
    }
}
