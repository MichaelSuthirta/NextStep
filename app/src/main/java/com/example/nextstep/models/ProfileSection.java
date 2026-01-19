package com.example.nextstep.models;

public class ProfileSection {
    private int id;
    private int userId;
    private String name;

    public ProfileSection(int id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public ProfileSection(int userId, String name) {
        this(0, userId, name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
