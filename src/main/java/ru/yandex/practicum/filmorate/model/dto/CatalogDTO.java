package ru.yandex.practicum.filmorate.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CatalogDTO(
        @NotNull @Positive Long id,
        @Nullable String name
) {
}
