package ru.yandex.practicum.filmorate.storage.catalog;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface CatalogStorage {

    List<Genre> getGenres();

    Genre getGenre(Long genreId);

    List<Mpa> getMpas();

    Mpa getMpa(Long mpaId);
}
