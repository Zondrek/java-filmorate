package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Long upsert(Film film) {
        if (film.getId() == null) {
            Long id = createId();
            film.setId(id);
            film.setUserLikes(new HashSet<>());
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
        getFilm(filmId).getUserLikes().add(userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        getFilm(filmId).getUserLikes().remove(userId);
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
