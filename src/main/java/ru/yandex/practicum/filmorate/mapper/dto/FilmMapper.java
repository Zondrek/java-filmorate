package ru.yandex.practicum.filmorate.mapper.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dto.CatalogDTO;
import ru.yandex.practicum.filmorate.model.dto.FilmDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static Film mapToModel(FilmDTO.Request dto) {
        return Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .mpaId(dto.getMpa().getId())
                .genreIds(mapToIds(dto.getGenres()))
                .build();
    }

    public static FilmDTO.Response mapToDTO(Film film) {
        return FilmDTO.Response.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
    }

    private static Set<Long> mapToIds(List<CatalogDTO> catalog) {
        return catalog.stream()
                .map(CatalogDTO::getId)
                .collect(Collectors.toSet());
    }
}
