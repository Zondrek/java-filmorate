package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.dto.FilmDTO;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.group.ValidationGroup;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService service;

    @PostMapping
    @Validated(ValidationGroup.OnCreate.class)
    public FilmDTO.Response addFilm(@Valid @RequestBody FilmDTO.Request dto) {
        return service.addFilm(dto);
    }

    @PutMapping
    @Validated(ValidationGroup.OnUpdate.class)
    public FilmDTO.Response updateFilm(@Valid @RequestBody FilmDTO.Request dto) {
        return service.updateFilm(dto);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return service.getFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable(name = "id") @NotNull Long filmId, @PathVariable @NotNull Long userId) {
        service.like(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable(name = "id") @NotNull Long filmId, @PathVariable @NotNull Long userId) {
        service.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return service.getPopularFilms(count);
    }
}
