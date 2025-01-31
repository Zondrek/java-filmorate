package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        Long id = createId();
        film.setId(id);
        film.setUserLikes(new HashSet<>());
        filmStorage.upsert(film);
        return film;
    }

    public Film updateFilm(Film film) {
        Film oldFilm = filmStorage.getFilm(film.getId());
        Film result = update(oldFilm, film);
        filmStorage.upsert(result);
        return result;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void like(Long filmId, Long userId) {
        User user = userStorage.getUser(userId);
        filmStorage.addLike(filmId, user.getId());
    }

    public void removeLike(Long filmId, Long userId) {
        User user = userStorage.getUser(userId);
        filmStorage.removeLike(filmId, user.getId());
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparingInt(f -> -f.getUserLikes().size()))
                .limit(count)
                .toList();
    }

    private long createId() {
        long currentMaxId = filmStorage.getFilms()
                .stream()
                .map(Film::getId)
                .max(Long::compareTo)
                .orElse(0L);
        return ++currentMaxId;
    }

    private Film update(Film originalFilm, Film newFilm) {
        return Film.builder()
                .id(newFilm.getId())
                .name(newFilm.getName() == null ? originalFilm.getName() : newFilm.getName())
                .description(newFilm.getDescription() == null ? originalFilm.getDescription() : newFilm.getDescription())
                .duration(newFilm.getDuration() == null ? originalFilm.getDuration() : newFilm.getDuration())
                .releaseDate(newFilm.getReleaseDate() == null ? originalFilm.getReleaseDate() : newFilm.getReleaseDate())
                .userLikes(originalFilm.getUserLikes())
                .build();
    }
}
