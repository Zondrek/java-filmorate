package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.row.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Primary
@Component
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String CREATE_FILM_QUERY = "INSERT INTO films " +
            "(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String GET_FILMS_QUERY = "SELECT f.id AS film_id, f.name, f.description, f.release_date, " +
            "f.duration, mpa.id AS mpa_id, mpa.name AS mpa_name, " +
            "(SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) AS likes, " +
            "(SELECT STRING_AGG(CONCAT(fg.genre_id, ':', gt.name), ',') " +
            "FROM film_genres fg " +
            "JOIN genres gt ON gt.id = fg.genre_id " +
            "WHERE fg.film_id = f.id) AS genres " +
            "FROM films f " +
            "JOIN mpas mpa ON f.mpa_id = mpa.id";
    private static final String GET_FILM_QUERY = "SELECT f.id AS film_id, f.name, f.description, f.release_date, " +
            "f.duration, mpa.id AS mpa_id, mpa.name AS mpa_name, " +
            "(SELECT COUNT(*) FROM likes l WHERE l.film_id = f.id) AS likes, " +
            "(SELECT STRING_AGG(CONCAT(fg.genre_id, ':', gt.name), ',') " +
            "FROM film_genres fg " +
            "JOIN genres gt ON gt.id = fg.genre_id " +
            "WHERE fg.film_id = f.id) AS genres " +
            "FROM films f " +
            "JOIN mpas mpa ON f.mpa_id = mpa.id " +
            "WHERE f.id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmRowMapper) {
        super(jdbc, filmRowMapper);
    }

    @Override
    @Transactional
    public Long upsert(Film film) {
        if (film.getId() == null) {
            Integer filmId = insert(CREATE_FILM_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());
            Long id = Long.valueOf(filmId);
            insertGenres(id, film.getGenres());
            return id;
        } else {
            update(UPDATE_FILM_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            updateGenres(film.getId(), film.getGenres());
            return film.getId();
        }
    }

    @Override
    public List<Film> getFilms() {
        return findMany(GET_FILMS_QUERY);
    }

    @Override
    public Film getFilm(long filmId) {
        checkFilmExistOrThrow(filmId);
        return findOne(GET_FILM_QUERY, filmId);
    }

    @Override
    public void addLike(long filmId, long userId) {
        checkFilmExistOrThrow(filmId);
        update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        checkFilmExistOrThrow(filmId);
        remove(REMOVE_LIKE_QUERY, filmId, userId);
    }

    private void updateGenres(Long filmId, Collection<Genre> genres) {
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
        insertGenres(filmId, genres);
    }

    private void insertGenres(Long filmId, Collection<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : genres) {
                batchArgs.add(new Object[]{filmId, genre.getId()});
            }
            jdbc.batchUpdate(INSERT_FILM_GENRE_QUERY, batchArgs);
        }
    }

    public void checkFilmExistOrThrow(long filmId) {
        String sqlQuery = "SELECT COUNT(*) FROM films WHERE id = ?";
        int count = jdbc.queryForObject(sqlQuery, Integer.class, filmId);
        if (count == 0) {
            throw new NotFoundException("Фильм с таким идентификатором не существует");
        }
    }
}
