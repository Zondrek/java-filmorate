package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.CatalogService;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping
public class CatalogController {

    private final CatalogService service;

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        return service.getGenres();
    }

    @GetMapping("/genres/{genreId}")
    public Genre getGenre(@PathVariable @NotNull Long genreId) {
        return service.getGenre(genreId);
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpas() {
        return service.getMpas();
    }

    @GetMapping("/mpa/{mpaId}")
    public Mpa getMpa(@PathVariable @NotNull Long mpaId) {
        return service.getMpa(mpaId);
    }
}
