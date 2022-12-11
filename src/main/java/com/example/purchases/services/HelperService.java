package com.example.purchases.services;

import com.example.purchases.dao.PurchaseDAO;
import com.example.purchases.dao.UserDAO;
import com.example.purchases.models.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;

public class HelperService {
    @Autowired
    UserDAO userDAO;

    public static String getSessionId(Cookie[] cookies) {
        String sessionId = null;
        if (cookies != null)
            for(Cookie cookie: cookies) {
                if (cookie.getName().equals("sessionId")) {
                    sessionId = cookie.getValue();
                    break;
                }
            }

        return sessionId;
    }

    public Boolean isUserExists(String login){
        UserProfile user = userDAO.getUserByLogin(login);
        return user != null;
    }
}
