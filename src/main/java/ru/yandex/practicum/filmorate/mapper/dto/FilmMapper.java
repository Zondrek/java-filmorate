package ru.yandex.practicum.filmorate.mapper.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.dto.CatalogDTO;
import ru.yandex.practicum.filmorate.model.dto.FilmDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToModel(FilmDTO dto) {
        return Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .mpa(mapToMpa(dto.getMpa()))
                .genres(mapToGenres(dto.getGenres()))
                .build();
    }

    public static FilmDTO mapToDTO(Film film) {
        return FilmDTO.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(mapToDTO(film.getMpa()))
                .genres(mapToCatalogItems(film.getGenres()))
                .build();
    }

    private static List<CatalogDTO> mapToCatalogItems(Set<Genre> genres) {
        return genres.stream()
                .map(genre -> new CatalogDTO(genre.getId(), genre.getName()))
                .collect(Collectors.toList());
    }

    private static Set<Genre> mapToGenres(List<CatalogDTO> list) {
        if (list == null || list.isEmpty()) return Set.of();
        return list.stream()
                .map(FilmMapper::mapToGenre)
                .collect(Collectors.toSet());
    }

    private static Genre mapToGenre(CatalogDTO catalog) {
        return Genre.builder()
                .id(catalog.id())
                .name(catalog.name())
                .build();
    }

    private static Mpa mapToMpa(CatalogDTO catalog) {
        return Mpa.builder()
                .id(catalog.id())
                .name(catalog.name())
                .build();
    }

    private static CatalogDTO mapToDTO(Mpa mpa) {
        return CatalogDTO.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .build();
    }
}
