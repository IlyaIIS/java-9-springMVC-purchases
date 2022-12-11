package com.example.purchases.services;

import com.example.purchases.dao.UserDAO;
import com.example.purchases.models.UserProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.Cookie;

@RunWith(SpringRunner.class)
@SpringBootTest
class HelperServiceTests {
    @InjectMocks
    private HelperService helperService;

    @Mock
    private UserDAO userDAO;

    @Test
    void testGetSessionId() {
        Cookie[] cookies = new Cookie[] {new Cookie("other", "-1"), new Cookie("sessionId", "111")};
        String sessionId = HelperService.getSessionId(cookies);
        Assertions.assertEquals("111", sessionId);
    }

    @Test
    void testIsUserExists() {
        Mockito.when(userDAO.getUserByLogin("111")).thenReturn(new UserProfile("111"));

        Boolean isUserExists = helperService.isUserExists("111");
        Assertions.assertTrue(isUserExists);

        isUserExists = helperService.isUserExists("100");
        Assertions.assertFalse(isUserExists);
    }
}