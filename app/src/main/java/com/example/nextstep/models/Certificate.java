package com.example.nextstep.models;

import android.os.Parcel;

import androidx.annotation.NonNull;

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

    protected Certificate(Parcel in){
        super(in);
        title = in.readString();
        publisher = in.readString();
        publishDate = in.readString();
        expireDate = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(publisher);
        dest.writeString(publishDate);
        dest.writeString(expireDate);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    public static final Creator<Certificate> CREATOR = new Creator<Certificate>() {
        @Override
        public Certificate createFromParcel(Parcel in) {
            return new Certificate(in);
        }

        @Override
        public Certificate[] newArray(int size) {
            return new Certificate[size];
        }
    };
}
