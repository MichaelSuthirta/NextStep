package com.example.nextstep.models;

public class Experience extends Post{
    private String title;
    private String start;
    private String finish;
    private String location;

    public Experience(String userID, String title, String start, String finish, String location) {
        super(userID);
        this.title = title;
        this.start = start;
        this.finish = finish;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
