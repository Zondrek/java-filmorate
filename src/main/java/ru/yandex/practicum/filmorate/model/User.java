package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.group.ValidationGroup;

import java.time.LocalDate;

@Data
@Builder
public class User {

    @Null(groups = ValidationGroup.OnCreate.class)
    @NotNull(groups = ValidationGroup.OnUpdate.class)
    @Positive
    private Long id;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    @Pattern(regexp = "\\S+")
    @Email
    private String email;

    @NotNull(groups = ValidationGroup.OnCreate.class)
    @Pattern(regexp = "\\S+")
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;
}
