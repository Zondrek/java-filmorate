package ru.yandex.practicum.filmorate.mapper.row;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    private static final String DELIMITER = ",";

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Обработка лайков
        String likeUserIds = rs.getString("likes");
        Set<Long> likes = new HashSet<>();
        if (likeUserIds != null && !likeUserIds.isEmpty()) {
            likes = Arrays.stream(likeUserIds.split(DELIMITER))
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
        }

        // Обработка жанров
        String genreIds = rs.getString("genres");
        Set<Long> genres = new HashSet<>();
        if (genreIds != null && !genreIds.isEmpty()) {
            genres = Arrays.stream(genreIds.split(DELIMITER))
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
        }

        // Создание объекта Film
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpaId(rs.getLong("mpa_id"))
                .userLikes(likes)
                .genreIds(genres)
                .build();
    }
}
