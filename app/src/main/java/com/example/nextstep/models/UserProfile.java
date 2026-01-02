package com.example.nextstep.models;

import java.util.ArrayList;

public class UserProfile {
    private String userID, description, profileImgPath, bannerImgPath;
    private ArrayList<String> skills;

    public UserProfile(String userID, String description, String profileImgPath, String bannerImgPath, ArrayList<String> skills) {
        this.userID = userID;
        this.description = description;
        this.profileImgPath = profileImgPath;
        this.bannerImgPath = bannerImgPath;
        this.skills = skills;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImgPath() {
        return profileImgPath;
    }

    public void setProfileImgPath(String profileImgPath) {
        this.profileImgPath = profileImgPath;
    }

    public String getBannerImgPath() {
        return bannerImgPath;
    }

    public void setBannerImgPath(String bannerImgPath) {
        this.bannerImgPath = bannerImgPath;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }
}
