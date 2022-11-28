package com.example.purchases.dao;

import com.example.purchases.models.Purchase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurchaseDAO {
    private final JdbcTemplate jdbcTemplate;

    public PurchaseDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer addPurchase(Purchase purchase) {
        jdbcTemplate.update(String.format("INSERT purchases(name, user_login) VALUES ('%s', '%s');", purchase.getName(), purchase.getUserLogin()));
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID();", (result, rowNum) -> result.getInt(1));
    }

    public List<Purchase> getPurchasesByUserLogin(String login) {
        return jdbcTemplate.query("SELECT * FROM purchases WHERE user_login = '" + login + "';", (result, rowNum) ->
            new Purchase(result.getInt("id"), result.getString("name"), result.getString("user_login"), result.getBoolean("marked")));
    }

    public void setPurchaseMark(int id, boolean isMarked) {
        jdbcTemplate.update(String.format("UPDATE purchases SET marked = %d WHERE id = %d;", isMarked ? 1 : 0, id));
    }

    public void deletePurchase(int id) {
        jdbcTemplate.update(String.format("DELETE FROM purchases WHERE id = %d", id));
    }
}
