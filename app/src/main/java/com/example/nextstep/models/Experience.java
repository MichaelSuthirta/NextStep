package com.example.nextstep.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Experience extends Post implements Parcelable {
    private String title;
    private String companyName;
    private String role;
    private String start;
    private String finish;
    private String location;

    public Experience(String userID, String companyName, String role, String start, String finish, String location) {
        super(userID);
        this.title = role + " at " + companyName;
        this.companyName = companyName;
        this.role = role;
        this.start = start;
        this.finish = finish;
        this.location = location;
    }

    protected Experience(Parcel in) {
        super(in);
        companyName = in.readString();
        role = in.readString();
        start = in.readString();
        finish = in.readString();
        location = in.readString();
        title = role + " at " + companyName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(companyName);
        dest.writeString(role);
        dest.writeString(start);
        dest.writeString(finish);
        dest.writeString(location);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Experience> CREATOR = new Creator<Experience>() {
        @Override
        public Experience createFromParcel(Parcel in) {
            return new Experience(in);
        }

        @Override
        public Experience[] newArray(int size) {
            return new Experience[size];
        }
    };

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
