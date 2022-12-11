package com.example.purchases.dao;

import com.example.purchases.models.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDAO {
    private final Map<String, UserProfile> sessionIdToProfile;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        sessionIdToProfile = new HashMap<>();
    }

    public void addNewUser(UserProfile userProfile) {
        try {
            jdbcTemplate.update(String.format("INSERT user(login, pass, email) VALUES ('%s', '%s', '%s');", userProfile.getLogin(), userProfile.getPass(), userProfile.getEmail()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserProfile getUserByLogin(String login) {
        try {
            return jdbcTemplate.query("SELECT * FROM user WHERE login = '" + login + "';", result -> {
                if (result.next()) {
                    return new UserProfile(result.getString(1),
                            result.getString(2),
                            result.getString(3));
                }
                else {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public UserProfile getUserBySessionId(String sessionId) {
        return sessionIdToProfile.get(sessionId);
    }

    public void addSession(String sessionId, UserProfile userProfile) {
        sessionIdToProfile.put(sessionId, userProfile);
    }

    public void deleteSession(String sessionId) {
        sessionIdToProfile.remove(sessionId);
    }
}
