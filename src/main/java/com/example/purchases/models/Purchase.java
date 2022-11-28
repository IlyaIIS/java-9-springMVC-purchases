package com.example.purchases.models;

public class Purchase {
    private final Integer id;
    private final String name;
    private final String userLogin;
    private final Boolean isMarked;

    public Purchase(Integer id, String name, String userLogin, Boolean isMarked) {
        this.id = id;
        this.name = name;
        this.userLogin = userLogin;
        this.isMarked = isMarked;
    }

    public Purchase(String name, String userLogin) {
        this.id = -1;
        this.name = name;
        this.userLogin = userLogin;
        this.isMarked = false;
    }

    public Integer getId() { return id; }
    public String getName() {
        return name;
    }
    public String getUserLogin() {
        return userLogin;
    }
    public Boolean isMarked() {
        return isMarked;
    }
}
