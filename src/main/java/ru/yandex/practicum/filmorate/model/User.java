package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.group.ValidationGroup;

import java.time.LocalDate;
import java.util.Set;

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

    @JsonIgnore
    private Set<Long> friendIds; //id пользователей
}
