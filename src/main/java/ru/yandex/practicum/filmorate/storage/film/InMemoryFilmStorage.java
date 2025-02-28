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
    public Long upsert(Film film) {
        if (film.getId() == null) {
            Long id = createId();
            film.setId(id);
        }
        films.put(film.getId(), film);
        return film.getId();
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
        Film film = films.get(filmId);
        int likeCount = film.getLikeCount();
        film.setLikeCount(likeCount + 1);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        Film film = films.get(filmId);
        int likeCount = film.getLikeCount();
        film.setLikeCount(likeCount - 1);
    }

    private long createId() {
        long currentMaxId = getFilms()
                .stream()
                .map(Film::getId)
                .max(Long::compareTo)
                .orElse(0L);
        return ++currentMaxId;
    }
}
