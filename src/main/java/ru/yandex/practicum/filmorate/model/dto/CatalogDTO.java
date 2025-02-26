package ru.yandex.practicum.filmorate.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CatalogDTO {
    @NotNull
    @Positive
    Long id;
}
