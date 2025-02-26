package ru.yandex.practicum.filmorate.model.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.validation.group.ValidationGroup;
import ru.yandex.practicum.filmorate.validation.validator.DateAfter;

import java.time.LocalDate;
import java.util.List;

public enum FilmDTO {
    ;

    private interface Id {
        @Null(groups = ValidationGroup.OnCreate.class)
        @NotNull(groups = ValidationGroup.OnUpdate.class)
        @Positive
        Long getId();
    }

    private interface Name {
        @NotBlank(groups = ValidationGroup.OnCreate.class)
        String getName();
    }

    private interface Description {
        @Size(max = 200)
        String getDescription();
    }

    private interface ReleaseDate {
        @DateAfter(value = "1895-12-28")
        LocalDate getReleaseDate();
    }

    private interface Duration {
        @Positive
        Integer getDuration();
    }

    private interface Genre {
        List<CatalogDTO> getGenres();
    }

    private interface Mpa {
        CatalogDTO getMpa();
    }

    @Value
    public static class Request implements Id, Name, Description, ReleaseDate, Duration, Genre, Mpa {
        Long id;
        String name;
        String description;
        LocalDate releaseDate;
        Integer duration;
        List<CatalogDTO> genres;
        CatalogDTO mpa;
    }

    @Builder
    @Value
    public static class Response implements Id, Name, Description, ReleaseDate, Duration {
        Long id;
        String name;
        String description;
        LocalDate releaseDate;
        Integer duration;
    }
}
