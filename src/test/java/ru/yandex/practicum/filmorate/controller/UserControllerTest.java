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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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
@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String USERS_PATH = "/users";

    private User user;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Name")
                .login("Login")
                .email("email@email.com")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void createUser() throws Exception {
        String json = objectMapper.writeValueAsString(user);
        user.setId(1L);
        when(userService.createUser(any())).thenReturn(user);
        checkRequestData(json, user, HttpMethod.POST);
        when(userService.getUsers()).thenReturn(List.of(user));
        checkGetData(user);
    }

    @Test
    void createUserNegativeId() throws Exception {
        user.setId(-1L);
        checkStatusBeforePostInvalidData(user);
    }

    @Test
    void createUserEmptyEmail() throws Exception {
        user.setEmail("");
        checkStatusBeforePostInvalidData(user);
    }

    @Test
    void createUserWithoutAt() throws Exception {
        user.setEmail("email.ru");
        checkStatusBeforePostInvalidData(user);
    }

    @Test
    void createUserEmptyLogin() throws Exception {
        user.setLogin("");
        checkStatusBeforePostInvalidData(user);
    }

    @Test
    void createUserNullLogin() throws Exception {
        user.setLogin(null);
        checkStatusBeforePostInvalidData(user);
    }

    @Test
    void createUserLoginWith_() throws Exception {
        user.setLogin("log in");
        checkStatusBeforePostInvalidData(user);
    }

    @Test
    void createUserInvalidBirthday() throws Exception {
        user.setBirthday(LocalDate.now().plusDays(1));
        checkStatusBeforePostInvalidData(user);
    }

    @Test
    void updateUser() throws Exception {
        checkStatusBeforePostData(user);
        user.setId(1L);
        user.setName("New name");
        user.setEmail("newemail@email.ru");
        user.setLogin("newlogin");
        user.setBirthday(LocalDate.of(1980, 11, 10));
        String json = objectMapper.writeValueAsString(user);
        when(userService.updateUser(any())).thenReturn(user);
        checkRequestData(json, user, HttpMethod.PUT);
        when(userService.getUsers()).thenReturn(List.of(user));
        checkGetData(user);
    }

    @Test
    void updateUserNegativeId() throws Exception {
        checkStatusBeforePostData(user);
        user.setId(-1L);
        checkStatusBeforePutInvalidData(user);
    }

    @Test
    void updateUserEmptyEmail() throws Exception {
        checkStatusBeforePostData(user);
        user.setId(1L);
        user.setEmail("");
        checkStatusBeforePutInvalidData(user);
    }

    @Test
    void updateUserWithoutAt() throws Exception {
        checkStatusBeforePostData(user);
        user.setId(1L);
        user.setEmail("email.ru");
        checkStatusBeforePutInvalidData(user);
    }

    @Test
    void updateUserEmptyLogin() throws Exception {
        checkStatusBeforePostData(user);
        user.setId(1L);
        user.setLogin("");
        checkStatusBeforePutInvalidData(user);
    }

    @Test
    void updateUserLoginWith_() throws Exception {
        checkStatusBeforePostData(user);
        user.setId(1L);
        user.setLogin("log in");
        checkStatusBeforePutInvalidData(user);
    }

    @Test
    void updateUserInvalidBirthday() throws Exception {
        checkStatusBeforePostData(user);
        user.setId(1L);
        user.setBirthday(LocalDate.now().plusDays(1));
        checkStatusBeforePutInvalidData(user);
    }

    private void checkRequestData(String json, User user, HttpMethod method) throws Exception {
        mockMvc.perform(request(method, USERS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())));
    }

    private void checkGetData(User user) throws Exception {
        mockMvc.perform(get(USERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].login", is(user.getLogin())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$[0].birthday", is(user.getBirthday().toString())));
    }

    private void checkStatusBeforePostInvalidData(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post(USERS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(USERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private void checkStatusBeforePostData(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post(USERS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void checkStatusBeforePutInvalidData(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(put(USERS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}