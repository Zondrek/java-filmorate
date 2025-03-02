package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.row.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.row.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, UserDbStorage.class, UserRowMapper.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserStorage userStorage;

    private Film film1;
    private Film film2;

    @BeforeEach
    public void setUp() {
        film1 = Film.builder()
                .name("Test Film 1")
                .description("Test description 1")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpa(Mpa.builder().id(1L).name("Test").build())
                .genres(Set.of(Genre.builder().id(1L).name("Test").build()))
                .build();

        film2 = Film.builder()
                .name("Test Film 2")
                .description("Test description 2")
                .releaseDate(LocalDate.of(2014, 11, 7))
                .duration(169)
                .mpa(Mpa.builder().id(1L).name("Test").build())
                .genres(Set.of(Genre.builder().id(2L).name("Test").build()))
                .build();

    }

    @Test
    public void testUpsertFilm() {
        Long filmId = filmStorage.upsert(film1);

        assertNotNull(filmId);
        Film savedFilm = filmStorage.getFilm(filmId);

        assertNotNull(savedFilm);
        assertEquals(savedFilm.getName(), film1.getName());
    }

    @Test
    public void testGetFilms() {
        filmStorage.upsert(film1);
        filmStorage.upsert(film2);

        List<Film> films = filmStorage.getFilms();
        assertEquals(2, films.size());
    }

    @Test
    public void testGetFilm() {
        Long filmId = filmStorage.upsert(film2);
        Film foundFilm = filmStorage.getFilm(filmId);

        assertNotNull(foundFilm);
        assertEquals(foundFilm.getName(), film2.getName());
    }

    @Test
    public void testAddAndRemoveLike() {
        Long filmId = filmStorage.upsert(film1);
        User user = User.builder()
                .email("user1@example.com")
                .login("user1")
                .name("User One")
                .birthday(LocalDate.of(1991, 2, 2))
                .build();
        Long userId = userStorage.upsert(user);

        filmStorage.addLike(filmId, userId);
        Film likedFilm = filmStorage.getFilm(filmId);
        assertEquals(1, likedFilm.getLikeCount());

        filmStorage.removeLike(filmId, userId);
        likedFilm = filmStorage.getFilm(filmId);
        assertEquals(0, likedFilm.getLikeCount());
    }
}