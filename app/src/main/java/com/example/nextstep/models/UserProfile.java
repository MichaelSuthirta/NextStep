package com.example.nextstep.models;

import java.util.ArrayList;

public class UserProfile {
    private String userID;
    // Used as "About Me".
    private String description;
    // Role shown in profile header.
    private String role;
    // (Optional) kept for future use; currently images are stored using SharedPreferences.
    private String profileImgPath, bannerImgPath;
    private ArrayList<String> skills;

    public UserProfile(String userID, String role, String description, ArrayList<String> skills) {
        this.userID = userID;
        this.role = role;
        this.description = description;
        this.skills = skills;
    }

    // Backward-compatible constructor (kept so existing code won't break if referenced).
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
