package com.example.purchases;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"/test_db.properties"})
public class DBTests {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void TestDB() throws Exception {
        Cookie[] cookies = this.mockMvc.perform(post("/api/users/login").param("login", "11").param("pass", "11"))
                .andExpect(cookie().exists("sessionId"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"))
                .andReturn()
                .getResponse()
                .getCookies();

        Cookie sessionCookie = null;
        for (Cookie cookie: cookies) {
            if (cookie.getName() == "sessionId"){
                sessionCookie = cookie;
            }
        }
        
        this.mockMvc.perform(post("/api/purchases").param("purchaseName", "test_purchase").cookie(sessionCookie))
                        .andExpect(status().is3xxRedirection());
        
        String content = this.mockMvc.perform(get("/api/purchases").cookie(sessionCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test_purchase")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String purchaseId = content.split("\"name\":\"test_purchase\",\"id\":")[1].split(",")[0];

        this.mockMvc.perform(put("/api/purchases/" + purchaseId + "/mark").param("isMarked", "false").cookie(sessionCookie))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/api/purchases").cookie(sessionCookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("{\"name\":\"test_purchase\",\"id\":" + purchaseId + ",\"isMarked\":true}")));

        this.mockMvc.perform(delete("/api/purchases/" + purchaseId).cookie(sessionCookie))
                .andExpect(status().isOk());
    }
}
