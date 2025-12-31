package com.example.nextstep.models;

import java.time.LocalDateTime;

public class Post {
    private String postId, userId, content, imgPath;
    private String postDate;

    public Post(String postId, String userId, String content, String imgPath, String postDate) {
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

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }
}
