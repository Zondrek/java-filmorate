package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.group.ValidationGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> cache = new HashMap<>();

    @PostMapping
    @Validated(ValidationGroup.OnCreate.class)
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("POST /films {}", film);
        Long id = createId();
        film.setId(id);
        cache.put(id, film);
        return film;
    }

    @PutMapping
    @Validated(ValidationGroup.OnUpdate.class)
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("PUT /films {}", film);
        Long id = film.getId();
        if (id == null || !cache.containsKey(id)) {
            throw new NotFoundException("Фильма с таким идентификатором не существует");
        }
        Film result = update(cache.get(id), film);
        cache.put(result.getId(), result);
        return result;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("GET /films, films count: {}", cache.size());
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
        return Film.builder()
                .id(newFilm.getId())
                .name(newFilm.getName() == null ? originalFilm.getName() : newFilm.getName())
                .description(newFilm.getDescription() == null ? originalFilm.getDescription() : newFilm.getDescription())
                .duration(newFilm.getDuration() == null ? originalFilm.getDuration() : newFilm.getDuration())
                .releaseDate(newFilm.getReleaseDate() == null ? originalFilm.getReleaseDate() : newFilm.getReleaseDate())
                .build();
    }
}
