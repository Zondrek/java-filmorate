package ru.yandex.practicum.filmorate.model.dto;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}