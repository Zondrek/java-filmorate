package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    public FilmDTO addFilm(@Valid @RequestBody FilmDTO dto) {
        return service.addFilm(dto);
    }

    @PutMapping
    @Validated(ValidationGroup.OnUpdate.class)
    public FilmDTO updateFilm(@Valid @RequestBody FilmDTO dto) {
        return service.updateFilm(dto);
    }

    @GetMapping
    public Collection<FilmDTO> getFilms() {
        return service.getFilms();
    }

    @GetMapping("/{filmId}")
    public FilmDTO getFilm(@PathVariable @NotNull Long filmId) {
        return service.getFilm(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void like(@PathVariable @NotNull Long filmId, @PathVariable @NotNull Long userId) {
        service.like(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable @NotNull Long filmId, @PathVariable @NotNull Long userId) {
        service.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmDTO> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return service.getPopularFilms(count);
    }
}
