package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.catalog.CatalogStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CatalogStorage storage;

    public List<Genre> getGenres() {
        return storage.getGenres();
    }

    public Genre getGenre(long genreId) {
        return storage.getGenre(genreId);
    }

    public List<Mpa> getMpas() {
        return storage.getMpas();
    }

    public Mpa getMpa(long mpaId) {
        return storage.getMpa(mpaId);
    }
}
