package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
        testRequestAndGet(user, HttpMethod.POST);
    }

    @Test
    void createUserNegativeId() throws Exception {
        user.setId(-1L);
        testPostInvalidData(user);
    }

    @Test
    void createUserEmptyEmail() throws Exception {
        user.setEmail("");
        testPostInvalidData(user);
    }

    @Test
    void createUserWithoutAt() throws Exception {
        user.setEmail("email.ru");
        testPostInvalidData(user);
    }

    @Test
    void createUserEmptyLogin() throws Exception {
        user.setLogin("");
        testPostInvalidData(user);
    }

    @Test
    void createUserNullLogin() throws Exception {
        user.setLogin(null);
        testPostInvalidData(user);
    }

    @Test
    void createUserLoginWith_() throws Exception {
        user.setLogin("log in");
        testPostInvalidData(user);
    }

    @Test
    void createUserNullName() throws Exception {
        user.setName(null);
        testRotateName(user);
    }

    @Test
    void createUserEmptyName() throws Exception {
        user.setName("");
        testRotateName(user);
    }

    @Test
    void createUserInvalidBirthday() throws Exception {
        user.setBirthday(LocalDate.now().plusDays(1));
        testPostInvalidData(user);
    }

    @Test
    void updateUser() throws Exception {
        testPostData(user);
        user.setId(1L);
        user.setName("New name");
        user.setEmail("newemail@email.ru");
        user.setLogin("newlogin");
        user.setBirthday(LocalDate.of(1980, 11, 10));
        testRequestAndGet(user, HttpMethod.PUT);
    }

    @Test
    void updateUserNegativeId() throws Exception {
        testPostData(user);
        user.setId(-1L);
        testPutInvalidData(user);
    }

    @Test
    void updateUserEmptyEmail() throws Exception {
        testPostData(user);
        user.setId(1L);
        user.setEmail("");
        testPutInvalidData(user);
    }

    @Test
    void updateUserWithoutAt() throws Exception {
        testPostData(user);
        user.setId(1L);
        user.setEmail("email.ru");
        testPutInvalidData(user);
    }

    @Test
    void updateUserEmptyLogin() throws Exception {
        testPostData(user);
        user.setId(1L);
        user.setLogin("");
        testPutInvalidData(user);
    }

    @Test
    void updateUserLoginWith_() throws Exception {
        testPostData(user);
        user.setId(1L);
        user.setLogin("log in");
        testPutInvalidData(user);
    }

    @Test
    void updateUserInvalidBirthday() throws Exception {
        testPostData(user);
        user.setId(1L);
        user.setBirthday(LocalDate.now().plusDays(1));
        testPutInvalidData(user);
    }

    private void testRequestAndGet(User user, HttpMethod method) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(request(method, USERS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())));
        mockMvc.perform(get(USERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].login", is(user.getLogin())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$[0].birthday", is(user.getBirthday().toString())));
    }

    private void testPostInvalidData(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post(USERS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get(USERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private void testRotateName(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post(USERS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(user.getLogin())))
                .andExpect(jsonPath("$.login", is(user.getLogin())));
        mockMvc.perform(get(USERS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is(user.getLogin())))
                .andExpect(jsonPath("$[0].login", is(user.getLogin())));
    }

    private void testPostData(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post(USERS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void testPutInvalidData(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(put(USERS_PATH).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}