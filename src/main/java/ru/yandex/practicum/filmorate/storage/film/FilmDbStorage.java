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

    private static final String CREATE_FILM_QUERY = "INSERT INTO film_table " +
            "(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE film_table " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String GET_FILMS_QUERY = "SELECT f.id AS film_id, f.name, f.description, f.release_date, " +
            "f.duration, mpa.id AS mpa_id, mpa.name AS mpa_name, " +
            "(SELECT STRING_AGG(CAST(l.user_id AS VARCHAR), ',') FROM like_table l WHERE l.film_id = f.id) AS likes, " +
            "(SELECT STRING_AGG(CONCAT(fg.genre_id, ':', gt.name), ',') " +
            "FROM film_genre_table fg " +
            "JOIN genre_table gt ON gt.id = fg.genre_id " +
            "WHERE fg.film_id = f.id) AS genres " +
            "FROM film_table f " +
            "JOIN mpa_table mpa ON f.mpa_id = mpa.id";
    private static final String GET_FILM_QUERY = "SELECT f.id AS film_id, f.name, f.description, f.release_date, " +
            "f.duration, mpa.id AS mpa_id, mpa.name AS mpa_name, " +
            "(SELECT STRING_AGG(CAST(l.user_id AS VARCHAR), ',') FROM like_table l WHERE l.film_id = f.id) AS likes, " +
            "(SELECT STRING_AGG(CONCAT(fg.genre_id, ':', gt.name), ',') " +
            "FROM film_genre_table fg " +
            "JOIN genre_table gt ON gt.id = fg.genre_id " +
            "WHERE fg.film_id = f.id) AS genres " +
            "FROM film_table f " +
            "JOIN mpa_table mpa ON f.mpa_id = mpa.id " +
            "WHERE f.id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO like_table (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM like_table WHERE film_id = ? AND user_id = ?";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO film_genre_table (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genre_table WHERE film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper filmRowMapper) {
        super(jdbc, filmRowMapper);
    }

    @Override
    @Transactional
    public Long upsert(Film film) {
        Long mpaId = film.getMpa().getId();
        checkMpaExistsOrThrow(mpaId);
        if (film.getId() == null) {
            Integer filmId = insert(CREATE_FILM_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    mpaId);
            Long id = Long.valueOf(filmId);
            insertGenres(id, film.getGenres());
            return id;
        } else {
            update(UPDATE_FILM_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    mpaId,
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

    private void updateGenres(Long filmId, Collection<Genre> genres) {
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
        insertGenres(filmId, genres);
    }

    private void insertGenres(Long filmId, Collection<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : genres) {
                checkGenreExistsOrThrow(genre.getId());
                batchArgs.add(new Object[]{filmId, genre.getId()});
            }
            jdbc.batchUpdate(INSERT_FILM_GENRE_QUERY, batchArgs);
        }
    }

    private void checkMpaExistsOrThrow(Long mpaId) {
        String sqlQuery = "SELECT COUNT(*) FROM mpa_table WHERE id = ?";
        int count = jdbc.queryForObject(sqlQuery, Integer.class, mpaId);
        if (count == 0) {
            throw new NotFoundException("MPA с ID " + mpaId + " не существует");
        }
    }

    private void checkGenreExistsOrThrow(Long genreId) {
        String sqlQuery = "SELECT COUNT(*) FROM genre_table WHERE id = ?";
        int count = jdbc.queryForObject(sqlQuery, Integer.class, genreId);
        if (count == 0) {
            throw new NotFoundException("Жанр с ID " + genreId + " не существует");
        }
    }

    public void checkFilmExistsOrThrow(long filmId) {
        String sqlQuery = "SELECT COUNT(*) FROM film_table WHERE id = ?";
        int count = jdbc.queryForObject(sqlQuery, Integer.class, filmId);
        if (count == 0) {
            throw new NotFoundException("Фильм с таким идентификатором не существует");
        }
    }
}
