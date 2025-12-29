package com.example.nextstep.models;

import java.time.LocalDateTime;

public class Post {
    private String postId, userId, content, imgPath;
    private LocalDateTime postDate;

    public Post(String postId, String userId, String content, String imgPath, LocalDateTime postDate) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.imgPath = imgPath;
        this.postDate = postDate;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }
}
