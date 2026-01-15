package com.example.nextstep.models;

public class Certificate extends Post{
    private String title;
    private String publisher;
    private String publishDate;
    private String expireDate;

    public Certificate(String userId, String title, String publisher, String publishDate, String expireDate) {
        super(userId);
        this.title = title;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.expireDate = expireDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
