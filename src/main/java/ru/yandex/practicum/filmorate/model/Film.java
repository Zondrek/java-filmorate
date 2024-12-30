package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.group.ValidationGroup;
import ru.yandex.practicum.filmorate.validation.validator.DateAfter;

import java.time.LocalDate;

@Data
@Builder
public class Film {

    @Null(groups = ValidationGroup.OnCreate.class)
    @NotNull(groups = ValidationGroup.OnUpdate.class)
    @Positive
    private Long id;

    @NotBlank(groups = ValidationGroup.OnCreate.class)
    private String name;

    @Size(max = 200)
    private String description;

    @DateAfter(value = "1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private Integer duration;
}
