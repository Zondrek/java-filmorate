package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.dto.CatalogDTO;
import ru.yandex.practicum.filmorate.model.dto.FilmDTO;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WebMvcTest(FilmController.class)
class FilmControllerTest {

    private static final String FILMS_PATH = "/films";

    private FilmDTO film;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        film = FilmDTO.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(2020, 1, 1))
                .duration(100)
                .mpa(new CatalogDTO(1L, "test"))
                .build();
    }

    @Test
    void createFilm() throws Exception {
        String filmJson = objectMapper.writeValueAsString(film);
        film.setId(1L);
        when(filmService.addFilm(any())).thenReturn(film);
        checkRequestData(filmJson, film, HttpMethod.POST);
        when(filmService.getFilms()).thenReturn(List.of(film));
        checkGetData(film);
    }

    @Test
    void createFilmNegativeId() throws Exception {
        film.setId(-1L);
        checkStatusBeforePostInvalidData(film);
    }

    @Test
    void createFilmEmptyName() throws Exception {
        film.setName("");
        checkStatusBeforePostInvalidData(film);
    }

    @Test
    void createFilmDescriptionMoreThan200() throws Exception {
        film.setDescription("0".repeat(201));
        checkStatusBeforePostInvalidData(film);
    }

    @Test
    void createFilmIncorrectDate() throws Exception {
        film.setReleaseDate(LocalDate.of(1860, 10, 12));
        checkStatusBeforePostInvalidData(film);
    }

    @Test
    void createFilmNegativeDuration() throws Exception {
        film.setDuration(-1);
        checkStatusBeforePostInvalidData(film);
    }

    @Test
    void updateFilm() throws Exception {
        checkStatusBeforePostData(film);
        film.setId(1L);
        film.setName("New name");
        film.setDescription("New description");
        film.setReleaseDate(LocalDate.of(1965, 10, 10));
        film.setDuration(10);
        String filmJson = objectMapper.writeValueAsString(film);
        when(filmService.updateFilm(any())).thenReturn(film);
        checkRequestData(filmJson, film, HttpMethod.PUT);
        when(filmService.getFilms()).thenReturn(List.of(film));
        checkGetData(film);
    }

    @Test
    void updateFilmNegativeId() throws Exception {
        checkStatusBeforePostData(film);
        film.setId(-1L);
        checkStatusBeforePutInvalidData(film);
    }

    @Test
    void updateFilmDescriptionMoreThan200() throws Exception {
        checkStatusBeforePostData(film);
        film.setId(1L);
        film.setDescription("0".repeat(201));
        checkStatusBeforePutInvalidData(film);
    }

    @Test
    void updateFilmIncorrectDate() throws Exception {
        checkStatusBeforePostData(film);
        film.setId(1L);
        film.setReleaseDate(LocalDate.of(1860, 10, 12));
        checkStatusBeforePutInvalidData(film);
    }

    @Test
    void updateFilmNegativeDuration() throws Exception {
        checkStatusBeforePostData(film);
        film.setId(1L);
        film.setDuration(-1);
        checkStatusBeforePutInvalidData(film);
    }

    private void checkRequestData(String json, FilmDTO film, HttpMethod method) throws Exception {
        mockMvc.perform(request(method, FILMS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(film.getName())))
                .andExpect(jsonPath("$.description", is(film.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(film.getReleaseDate().toString())))
                .andExpect(jsonPath("$.duration", is(film.getDuration())));
    }

    private void checkGetData(FilmDTO film) throws Exception {
        mockMvc.perform(get(FILMS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(film.getName())))
                .andExpect(jsonPath("$[0].description", is(film.getDescription())))
                .andExpect(jsonPath("$[0].releaseDate", is(film.getReleaseDate().toString())))
                .andExpect(jsonPath("$[0].duration", is(film.getDuration())));
    }

    private void checkStatusBeforePostInvalidData(FilmDTO film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(post(FILMS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(FILMS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private void checkStatusBeforePostData(FilmDTO film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(post(FILMS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void checkStatusBeforePutInvalidData(FilmDTO film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(put(FILMS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}