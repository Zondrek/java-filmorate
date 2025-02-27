package ru.yandex.practicum.filmorate.storage.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CatalogDbStorage implements CatalogStorage {

    private static final String GET_GENRES_QUERY = "SELECT * FROM genre_table";
    private static final String GET_GENRE_QUERY = "SELECT * FROM genre_table WHERE id = ?";
    private static final String GET_MPAS_QUERY = "SELECT * FROM mpa_table";
    private static final String GET_MPA_QUERY = "SELECT * FROM mpa_table WHERE id = ?";
    private static final String CATALOG_EXCEPTION = "Каталога с таким идентификатором не существует";

    private final JdbcTemplate jdbc;

    @Override
    public List<Genre> getGenres() {
        return jdbc.query(GET_GENRES_QUERY, this::mapGenre);
    }

    @Override
    public Genre getGenre(Long genreId) {
        checkGenreExistsOrThrow(genreId);
        return jdbc.queryForObject(GET_GENRE_QUERY, this::mapGenre, genreId);
    }

    @Override
    public List<Mpa> getMpas() {
        return jdbc.query(GET_MPAS_QUERY, this::mapMpa);
    }

    @Override
    public Mpa getMpa(Long mpaId) {
        checkMpaExistsOrThrow(mpaId);
        return jdbc.queryForObject(GET_MPA_QUERY, this::mapMpa, mpaId);
    }

    private Genre mapGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

    private Mpa mapMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

    public void checkGenreExistsOrThrow(long genreId) {
        String sqlQuery = "SELECT COUNT(*) FROM genre_table WHERE id = ?";
        int count = jdbc.queryForObject(sqlQuery, Integer.class, genreId);
        if (count == 0) {
            throw new NotFoundException(CATALOG_EXCEPTION);
        }
    }

    public void checkMpaExistsOrThrow(long mpaId) {
        String sqlQuery = "SELECT COUNT(*) FROM mpa_table WHERE id = ?";
        int count = jdbc.queryForObject(sqlQuery, Integer.class, mpaId);
        if (count == 0) {
            throw new NotFoundException(CATALOG_EXCEPTION);
        }
    }
}
