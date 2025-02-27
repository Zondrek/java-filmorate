package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.mapper.dto.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.FilmDTO;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmDTO addFilm(FilmDTO dto) {
        Film film = FilmMapper.mapToModel(dto);
        Long id = filmStorage.upsert(film);
        film.setId(id);
        return FilmMapper.mapToDTO(film);
    }

    public FilmDTO updateFilm(FilmDTO dto) {
        Film newFilm = FilmMapper.mapToModel(dto);
        Film oldFilm = filmStorage.getFilm(newFilm.getId());
        Film result = update(oldFilm, newFilm);
        filmStorage.upsert(result);
        return FilmMapper.mapToDTO(result);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public FilmDTO getFilm(Long filmId) {
        Film film = filmStorage.getFilm(filmId);
        return FilmMapper.mapToDTO(film);
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

    private Film update(Film originalFilm, Film newFilm) {
        return Film.builder()
                .id(newFilm.getId())
                .name(newFilm.getName() == null ? originalFilm.getName() : newFilm.getName())
                .description(newFilm.getDescription() == null ? originalFilm.getDescription() : newFilm.getDescription())
                .duration(newFilm.getDuration() == null ? originalFilm.getDuration() : newFilm.getDuration())
                .releaseDate(newFilm.getReleaseDate() == null ? originalFilm.getReleaseDate() : newFilm.getReleaseDate())
                .userLikes(originalFilm.getUserLikes())
                .mpa(originalFilm.getMpa())
                .genres(originalFilm.getGenres())
                .build();
    }
}
