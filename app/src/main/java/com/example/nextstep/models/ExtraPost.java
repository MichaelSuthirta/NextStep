package com.example.nextstep.models;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class ExtraPost extends Post{
    private String title, organization, startDate, endDate, categoryID;

    public ExtraPost(String userId, String title, String organization, String startDate, String endDate, String categoryID) {
        super(userId);
        this.title = title;
        this.organization = organization;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryID = categoryID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    protected ExtraPost(Parcel in){
        super(in);
        title = in.readString();
        organization = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        categoryID = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(organization);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(categoryID);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    public static final Creator<ExtraPost> CREATOR = new Creator<ExtraPost>() {
        @Override
        public ExtraPost createFromParcel(Parcel in) {
            return new ExtraPost(in);
        }

        @Override
        public ExtraPost[] newArray(int size) {
            return new ExtraPost[size];
        }
    };
}
