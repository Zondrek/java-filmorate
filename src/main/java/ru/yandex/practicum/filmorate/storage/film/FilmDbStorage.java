package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.row.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseDbStorage;

import java.util.List;

@Primary
@Component
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String CREATE_FILM_QUERY = "INSERT INTO film_table " +
            "(name, description, release_date, duration, genre_id, mpa_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE film_table " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, genre_id = ?, mpa_id = ? WHERE id = ?";
    private static final String GET_FILMS_QUERY = "SELECT f.id AS film_id, f.name, f.description, f.release_date, f.duration, f.genre_id, f.mpa_id, " +
            "(SELECT STRING_AGG(CAST(l.user_id AS VARCHAR), ',') FROM like_table l WHERE l.film_id = f.id) AS likes " +
            "FROM film_table f";
    private static final String GET_FILM_QUERY = "SELECT f.id AS film_id, f.name, f.description, " +
            "f.release_date, f.duration, f.genre_id, f.mpa_id, " +
            "(SELECT STRING_AGG(CAST(l.user_id AS VARCHAR), ',') FROM like_table l WHERE l.film_id = f.id) AS likes " +
            "FROM film_table f WHERE f.id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO like_table (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM like_table WHERE film_id = ? AND user_id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmRowMapper) {
        super(jdbc, filmRowMapper);
    }

    @Override
    public Long upsert(Film film) {
        if (film.getId() == null) {
            Integer filmId = insert(CREATE_FILM_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpaId());
            return Long.valueOf(filmId);
        } else {
            update(UPDATE_FILM_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpaId(),
                    film.getId());
            return film.getId();
        }
    }

    @Override
    public List<Film> getFilms() {
        return findMany(GET_FILMS_QUERY);
    }

    @Override
    public Film getFilm(long filmId) {
        checkFilmExistsOrThrow(filmId);
        return findOne(GET_FILM_QUERY, filmId);
    }

    @Override
    public void addLike(long filmId, long userId) {
        checkFilmExistsOrThrow(filmId);
        update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        checkFilmExistsOrThrow(filmId);
        remove(REMOVE_LIKE_QUERY, filmId, userId);
    }

    public void checkFilmExistsOrThrow(long filmId) {
        String sqlQuery = "SELECT COUNT(*) FROM film_table WHERE id = ?";
        int count = jdbc.queryForObject(sqlQuery, Integer.class, filmId);
        if (count == 0) {
            throw new NotFoundException("Фильм с таким идентификатором не существует");
        }
    }
}
