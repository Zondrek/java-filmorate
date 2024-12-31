package ru.yandex.practicum.filmorate.model;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}