package ru.yandex.practicum.filmorate.mapper.row;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    private static final String COMMON_DELIMITER = ",";
    private static final String FIELD_DELIMITER = ":";


    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Обработка лайков
        String likeUserIds = rs.getString("likes");
        Set<Long> likes = new HashSet<>();
        if (likeUserIds != null && !likeUserIds.isEmpty()) {
            likes = Arrays.stream(likeUserIds.split(COMMON_DELIMITER))
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
        }

        // Обработка жанров
        String str = rs.getString("genres");
        Set<Genre> genres = new HashSet<>();
        if (str != null && !str.isEmpty()) {
            genres = Arrays.stream(str.split(COMMON_DELIMITER))
                    .map(genreStr -> {
                        String[] items = genreStr.split(FIELD_DELIMITER);
                        return Genre.builder()
                                .id(Long.valueOf(items[0]))
                                .name(items[1])
                                .build();
                    })
                    .collect(Collectors.toSet());
        }

        Mpa mpa = Mpa.builder()
                .id(Long.valueOf(rs.getString("mpa_id")))
                .name(rs.getString("mpa_name"))
                .build();

        // Создание объекта Film
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .userLikes(likes)
                .genres(genres)
                .build();
    }
}
