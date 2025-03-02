package ru.yandex.practicum.filmorate.storage.catalog;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.error.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class CatalogDbStorage implements CatalogStorage {

    private static final String GET_GENRES_QUERY = "SELECT * FROM genres";
    private static final String GET_GENRE_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String GET_MPAS_QUERY = "SELECT * FROM mpas";
    private static final String GET_MPA_QUERY = "SELECT * FROM mpas WHERE id = ?";
    private static final String CATALOG_EXCEPTION = "Каталога с таким идентификатором не существует";

    private final JdbcTemplate jdbc;

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public CatalogDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbc);
    }

    @Override
    public List<Genre> getGenres() {
        return jdbc.query(GET_GENRES_QUERY, this::mapGenre);
    }

    @Override
    public Genre getGenre(Long genreId) {
        checkGenresExistOrThrow(List.of(genreId));
        return jdbc.queryForObject(GET_GENRE_QUERY, this::mapGenre, genreId);
    }

    @Override
    public List<Mpa> getMpas() {
        return jdbc.query(GET_MPAS_QUERY, this::mapMpa);
    }

    @Override
    public Mpa getMpa(Long mpaId) {
        checkMpaExistOrThrow(mpaId);
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

    @Override
    public void checkGenresExistOrThrow(List<Long> genreIds) {
        if (genreIds.isEmpty()) {
            return;
        }
        String sqlQuery = "SELECT COUNT(*) FROM genres WHERE id IN (:genreIds)";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("genreIds", genreIds);
        Integer count = namedJdbcTemplate.queryForObject(sqlQuery, parameters, Integer.class);
        if (count == null || count != genreIds.size()) {
            throw new NotFoundException(CATALOG_EXCEPTION);
        }
    }

    @Override
    public void checkMpaExistOrThrow(long mpaId) {
        String sqlQuery = "SELECT COUNT(*) FROM mpas WHERE id = ?";
        int count = jdbc.queryForObject(sqlQuery, Integer.class, mpaId);
        if (count == 0) {
            throw new NotFoundException(CATALOG_EXCEPTION);
        }
    }
}
