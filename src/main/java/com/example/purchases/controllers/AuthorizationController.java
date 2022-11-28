package com.example.purchases.controllers;

import com.example.purchases.dao.UserDAO;
import com.example.purchases.models.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/authorization")
public class AuthorizationController {

    @Autowired
    private UserDAO userDAO;

    @GetMapping("")
    public String defaultRedirect() {
        return "authorization/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "authorization/login";
    }

    @GetMapping("/registration")
    public String showRegisterationPage() {
        return "authorization/registration";
    }

    @PostMapping("/login")
    public void tryAuthorize(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        String login = request.getParameter("login");
        String pass = request.getParameter("pass");

        response.setContentType("text/html;charset=utf-8");

        if (login == null || pass == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserProfile profile = userDAO.getUserByLogin(login);

        if (profile == null || !profile.getPass().equals(pass)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Cookie cookie = new Cookie("sessionId", request.getSession().getId());
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);

        userDAO.addSession(request.getSession().getId(), profile);

        response.sendRedirect(request.getContextPath() + "/home");
    }

    @PostMapping("/registration")
    public void doRegistration(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String login = request.getParameter("login");
        String pass = request.getParameter("pass");

        response.setContentType("text/html;charset=utf-8");

        if (login == null || pass == null || email == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        UserProfile profile = userDAO.getUserByLogin(login);

        if (profile == null) {
            userDAO.addNewUser(new UserProfile(login, pass, email));
            profile = userDAO.getUserByLogin(login);
        }
        else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (profile == null || !profile.getPass().equals(pass)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Cookie cookie = new Cookie("sessionId", request.getSession().getId());
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);

        userDAO.addSession(request.getSession().getId(), profile);

        response.sendRedirect(request.getContextPath() + "/home");
    }

    @PostMapping("/logout")
    private void logOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sessionId = request.getSession().getId();
        UserProfile profile = userDAO.getUserBySessionId(sessionId);
        if (profile == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            userDAO.deleteSession(sessionId);

            Cookie delCookie = null;
            for(Cookie cookie: request.getCookies()) {
                if (cookie.getName().equals("sessionId")) {
                    delCookie = cookie;
                    break;
                }
            }
            if (delCookie != null) {
                delCookie.setMaxAge(0);
                response.addCookie(delCookie);
            }

            response.sendRedirect(request.getContextPath() + "/authorization/login");
        }
    }
}
