package com.example.purchases.controllers;

import com.example.purchases.models.Purchase;
import com.example.purchases.dao.PurchaseDAO;
import com.example.purchases.dao.UserDAO;
import com.example.purchases.models.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    @Autowired
    PurchaseDAO purchaseDAO;

    @Autowired
    UserDAO userDAO;

    private String getSessionId(Cookie[] cookies) {
        String sessionId = null;
        for(Cookie cookie: cookies) {
            if (cookie.getName().equals("sessionId")) {
                sessionId = cookie.getValue();
                break;
            }
        }

        return sessionId;
    }

    private String validateUser(Cookie[] cookies, Model model) {
        String sessionId = getSessionId(cookies);

        if (sessionId == null) {
            model.addAttribute("error_text", "Error: wrong cookie");
            return "my_error";
        }

        UserProfile userProfile = userDAO.getUserBySessionId(sessionId);

        if (userProfile == null) {
            model.addAttribute("error_text","Error: user not found");
            return "my_error";
        }

        return null;
    }

    @GetMapping("")
    public String showHomePage(HttpServletRequest req, Model model) throws IOException {
        String sessionId = getSessionId(req.getCookies());

        if (sessionId == null) {
            model.addAttribute("error_text", "Error: wrong cookie");
            return "my_error";
        }

        UserProfile userProfile = userDAO.getUserBySessionId(sessionId);

        if (userProfile == null) {
            model.addAttribute("error_text","Error: user not found");
            return "my_error";
        }

        List<Purchase> purchases = purchaseDAO.getPurchasesByUserLogin(userProfile.getLogin());
        model.addAttribute("purchases", purchases);

        return "home/home";
    }

    @GetMapping("/add")
    public String showAddPurchasePage(HttpServletRequest req, Model model) {
        String validResult = validateUser(req.getCookies(), model);
        if (validResult != null)
            return validResult;

        return "home/add";
    }

    @PostMapping("/add")
    public void addPurchase(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sessionId = getSessionId(req.getCookies());
        if (sessionId == null)
            return;

        UserProfile userProfile = userDAO.getUserBySessionId(sessionId);
        if (userProfile == null)
            return;

        String name = req.getParameter("purchaseName");
        String login = userProfile.getLogin();

        purchaseDAO.addPurchase(new Purchase(name, login));

        resp.sendRedirect(req.getContextPath() + "/home");
    }

    @GetMapping("/delete")
    public void deletePurchase(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sessionId = getSessionId(req.getCookies());
        if (sessionId == null)
            return;

        UserProfile userProfile = userDAO.getUserBySessionId(sessionId);
        if (userProfile == null)
            return;

        int id = Integer.parseInt(req.getParameter("id"));

        purchaseDAO.deletePurchase(id);

        resp.sendRedirect(req.getContextPath() + "/home");
    }

    @GetMapping("/mark")
    public  void markPurchase(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sessionId = getSessionId(req.getCookies());
        if (sessionId == null)
            return;

        UserProfile userProfile = userDAO.getUserBySessionId(sessionId);
        if (userProfile == null)
            return;

        int id = Integer.parseInt(req.getParameter("id"));
        boolean isMarked = Boolean.parseBoolean(req.getParameter("isMarked"));

        purchaseDAO.setPurchaseMark(id, !isMarked);

        resp.sendRedirect(req.getContextPath() + "/home");
    }
}
