package com.example.purchases.controllers;

import com.example.purchases.models.Purchase;
import com.example.purchases.dao.PurchaseDAO;
import com.example.purchases.dao.UserDAO;
import com.example.purchases.models.UserProfile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.example.purchases.services.HelperService.getSessionId;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    PurchaseDAO purchaseDAO;

    @Autowired
    UserDAO userDAO;

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

    @GetMapping("/home")
    public String showHomePage(HttpServletRequest req, Model model, HttpServletResponse resp) {
        String sessionId = getSessionId(req.getCookies());

        if (sessionId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            model.addAttribute("error_text", "Error: wrong cookie");
            return "my_error";
        }

        UserProfile userProfile = userDAO.getUserBySessionId(sessionId);

        if (userProfile == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            model.addAttribute("error_text","Error: user not found");
            return "my_error";
        }

        return "home/home";
    }

    @GetMapping("/home/add")
    public String showAddPurchasePage(HttpServletRequest req, Model model, HttpServletResponse resp) {
        String validResult = validateUser(req.getCookies(), model);
        if (validResult != null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return validResult;
        }

        return "home/add";
    }

    @PostMapping("/api/purchases")
    public void addPurchase(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sessionId = getSessionId(req.getCookies());
        if (sessionId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserProfile userProfile = userDAO.getUserBySessionId(sessionId);
        if (userProfile == null){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String name = req.getParameter("purchaseName");
        String login = userProfile.getLogin();

        purchaseDAO.addPurchase(new Purchase(name, login));

        resp.sendRedirect(req.getContextPath() + "/home");
    }

    @DeleteMapping("/api/purchases/{id}")
    public void deletePurchase(@PathVariable(value = "id") int id, HttpServletRequest req, HttpServletResponse resp) {
        String sessionId = getSessionId(req.getCookies());
        if (sessionId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserProfile userProfile = userDAO.getUserBySessionId(sessionId);
        if (userProfile == null){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        purchaseDAO.deletePurchase(id);
    }

    @PutMapping("/api/purchases/{id}/mark")
    public void markPurchase(@PathVariable(value = "id") int id, HttpServletRequest req, HttpServletResponse resp) {
        String sessionId = getSessionId(req.getCookies());
        if (sessionId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserProfile userProfile = userDAO.getUserBySessionId(sessionId);
        if (userProfile == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        boolean isMarked = Boolean.parseBoolean(req.getParameter("isMarked"));

        purchaseDAO.setPurchaseMark(id, !isMarked);
    }

    @ResponseBody
    @GetMapping(value = "/api/purchases", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String returnPurchases(HttpServletRequest req, HttpServletResponse resp) throws JSONException {
        String sessionId = getSessionId(req.getCookies());
        if (sessionId == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "false";
        }

        UserProfile userProfile = userDAO.getUserBySessionId(sessionId);
        if (userProfile == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "false";
        }

        JSONObject json = new JSONObject();
        JSONArray jarray = new JSONArray();
        List<Purchase> purchases = purchaseDAO.getPurchasesByUserLogin(userProfile.getLogin());
        for (Purchase purchase: purchases) {
            JSONObject jobj = new JSONObject();
            jobj.put("name", purchase.getName());
            jobj.put("id", purchase.getId());
            jobj.put("isMarked", purchase.isMarked());

            jarray.put(jobj);
        }
        json.put("purchases", jarray);

        return json.toString();
    }
}
