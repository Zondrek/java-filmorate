package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WebMvcTest(FilmController.class)
class FilmControllerTest {

    private static final String FILMS_PATH = "/films";

    private Film film;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public FilmStorage filmStorage() {
            return new InMemoryFilmStorage();
        }

        @Bean
        public UserStorage userStorage() {
            return new InMemoryUserStorage();
        }


        @Bean
        public FilmService filmService(
                FilmStorage filmStorage,
                UserStorage userStorage
        ) {
            return new FilmService(filmStorage, userStorage);
        }
    }

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .build();
    }

    @Test
    void createFilm() throws Exception {
        testRequestAndGet(film, HttpMethod.POST);
    }

    @Test
    void createFilmNegativeId() throws Exception {
        film.setId(-1L);
        testPostInvalidData(film);
    }

    @Test
    void createFilmEmptyName() throws Exception {
        film.setName("");
        testPostInvalidData(film);
    }

    @Test
    void createFilmDescriptionMoreThan200() throws Exception {
        film.setDescription("0".repeat(201));
        testPostInvalidData(film);
    }

    @Test
    void createFilmIncorrectDate() throws Exception {
        film.setReleaseDate(LocalDate.of(1860, 10, 12));
        testPostInvalidData(film);
    }

    @Test
    void createFilmNegativeDuration() throws Exception {
        film.setDuration(-1);
        testPostInvalidData(film);
    }

    @Test
    void updateFilm() throws Exception {
        testPostData(film);
        film.setId(1L);
        film.setName("New name");
        film.setDescription("New description");
        film.setReleaseDate(LocalDate.of(1965, 10, 10));
        film.setDuration(10);
        testRequestAndGet(film, HttpMethod.PUT);
    }

    @Test
    void updateFilmNegativeId() throws Exception {
        testPostData(film);
        film.setId(-1L);
        testPutInvalidData(film);
    }

    @Test
    void updateFilmDescriptionMoreThan200() throws Exception {
        testPostData(film);
        film.setId(1L);
        film.setDescription("0".repeat(201));
        testPutInvalidData(film);
    }

    @Test
    void updateFilmIncorrectDate() throws Exception {
        testPostData(film);
        film.setId(1L);
        film.setReleaseDate(LocalDate.of(1860, 10, 12));
        testPutInvalidData(film);
    }

    @Test
    void updateFilmNegativeDuration() throws Exception {
        testPostData(film);
        film.setId(1L);
        film.setDuration(-1);
        testPutInvalidData(film);
    }

    private void testRequestAndGet(Film film, HttpMethod method) throws Exception {
        String filmJson = objectMapper.writeValueAsString(film);
        mockMvc.perform(request(method, FILMS_PATH).content(filmJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(film.getName())))
                .andExpect(jsonPath("$.description", is(film.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(film.getReleaseDate().toString())))
                .andExpect(jsonPath("$.duration", is(film.getDuration())));
        mockMvc.perform(get(FILMS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(film.getName())))
                .andExpect(jsonPath("$[0].description", is(film.getDescription())))
                .andExpect(jsonPath("$[0].releaseDate", is(film.getReleaseDate().toString())))
                .andExpect(jsonPath("$[0].duration", is(film.getDuration())));
    }

    private void testPostInvalidData(Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(post(FILMS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(FILMS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private void testPostData(Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(post(FILMS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void testPutInvalidData(Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(put(FILMS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}