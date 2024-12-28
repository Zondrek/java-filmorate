package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.DateAfter;

import java.time.LocalDate;

@Data
@Builder
public class Film {

    @Positive
    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @DateAfter(date = "1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private Integer duration;
}
