package com.example.purchases;

import com.example.purchases.controllers.AuthorizationController;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test_db.properties")
public class PagesTests {
    @Autowired
    private AuthorizationController controller;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testExternalPages() throws Exception {
        Assertions.assertThat(controller).isNotNull();

        this.mockMvc.perform(get("/authorization"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Введите логин и пароль")))
                .andExpect(content().string(containsString("<title>Авторизация</title>")));

        this.mockMvc.perform(get("/authorization/registration"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Введите email, логин и пароль")))
                .andExpect(content().string(containsString("<title>Регистрация</title>")));
    }

    @Test
    public void testWrongAccess() throws Exception {
        this.mockMvc.perform(get("/home"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Error: wrong cookie")));

        this.mockMvc.perform(post("/api/users/login").param("login", "22").param("pass", "11"))
                .andExpect(cookie().doesNotExist("sessionId"))
                .andExpect(status().is4xxClientError());

        this.mockMvc.perform(get("/home").cookie(new Cookie("sessionId", "-5")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Error: user not found")));

        this.mockMvc.perform(get("/home").cookie(new Cookie("sessionId", "-5")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Error: user not found")));
    }

    @Test
    public void testInternalPages() throws Exception {
        Cookie[] cookies = this.mockMvc.perform(post("/api/users/login").param("login", "11").param("pass", "11"))
                .andExpect(cookie().exists("sessionId"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andReturn()
                .getResponse()
                .getCookies();

        Cookie sessionCoolie = null;
        for (Cookie cookie: cookies) {
            if (cookie.getName() == "sessionId"){
                sessionCoolie = cookie;
            }
        }

        mockMvc.perform(get("/home").cookie(sessionCoolie))
                .andExpect(status().isOk());

        mockMvc.perform(get("/home/add").cookie(sessionCoolie))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/purchases").cookie(sessionCoolie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"purchases\":[")));
    }
}
