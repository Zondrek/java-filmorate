package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.mapper.dto.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.FilmDTO;
import ru.yandex.practicum.filmorate.storage.catalog.CatalogStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final CatalogStorage catalogStorage;

    public FilmDTO addFilm(FilmDTO dto) {
        Film film = FilmMapper.mapToModel(dto);
        checkCatalogsExistOrThrow(film);
        Long id = filmStorage.upsert(film);
        film.setId(id);
        return FilmMapper.mapToDTO(film);
    }

    public FilmDTO updateFilm(FilmDTO dto) {
        Film newFilm = FilmMapper.mapToModel(dto);
        checkCatalogsExistOrThrow(newFilm);
        Film oldFilm = filmStorage.getFilm(newFilm.getId());
        Film result = update(oldFilm, newFilm);
        filmStorage.upsert(result);
        return FilmMapper.mapToDTO(result);
    }

    public Collection<FilmDTO> getFilms() {
        return filmStorage.getFilms().stream()
                .map(FilmMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public FilmDTO getFilm(long filmId) {
        Film film = filmStorage.getFilm(filmId);
        return FilmMapper.mapToDTO(film);
    }

    public void like(long filmId, long userId) {
        User user = userStorage.getUser(userId);
        filmStorage.addLike(filmId, user.getId());
    }

    public void removeLike(long filmId, long userId) {
        User user = userStorage.getUser(userId);
        filmStorage.removeLike(filmId, user.getId());
    }

    public List<FilmDTO> getPopularFilms(int count) {
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikeCount()))
                .limit(count)
                .map(FilmMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    private void checkCatalogsExistOrThrow(Film film) {
        List<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .toList();
        catalogStorage.checkGenresExistOrThrow(genreIds);
        catalogStorage.checkMpaExistOrThrow(film.getMpa().getId());
    }

    private Film update(Film originalFilm, Film newFilm) {
        return Film.builder()
                .id(newFilm.getId())
                .name(newFilm.getName() == null ? originalFilm.getName() : newFilm.getName())
                .description(newFilm.getDescription() == null ? originalFilm.getDescription() : newFilm.getDescription())
                .duration(newFilm.getDuration() == null ? originalFilm.getDuration() : newFilm.getDuration())
                .releaseDate(newFilm.getReleaseDate() == null ? originalFilm.getReleaseDate() : newFilm.getReleaseDate())
                .likeCount(originalFilm.getLikeCount())
                .mpa(originalFilm.getMpa())
                .genres(originalFilm.getGenres())
                .build();
    }
}
