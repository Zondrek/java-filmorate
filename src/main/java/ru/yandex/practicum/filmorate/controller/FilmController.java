package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> cache = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        Long id = film.getId();
        if (id == null) {
            id = createId();
        } else if (cache.containsKey(id)) {
            throw new ValidationException("Фильм с таким идентификатором уже существует");
        }
        film.setId(id);
        cache.put(id, film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Long id = film.getId();
        if (id == null || !cache.containsKey(id)) {
            throw new ValidationException("Фильма с таким идентификатором не существует");
        }
        Film result = update(cache.get(id), film);
        cache.put(result.getId(), result);
        return result;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return cache.values();
    }

    private long createId() {
        long currentMaxId = cache.keySet()
                .stream()
                .max(Long::compareTo)
                .orElse(0L);
        return ++currentMaxId;
    }

    private Film update(Film originalFilm, Film newFilm) {
        Film.FilmBuilder builder = Film.builder();
        builder.id(newFilm.getId());
        builder.name(newFilm.getName() == null ? originalFilm.getName() : newFilm.getName());
        builder.description(newFilm.getDescription() == null ? originalFilm.getDescription() : newFilm.getDescription());
        builder.duration(newFilm.getDuration() == null ? originalFilm.getDuration() : newFilm.getDuration());
        builder.releaseDate(newFilm.getReleaseDate() == null ? originalFilm.getReleaseDate() : newFilm.getReleaseDate());
        return builder.build();
    }
}
