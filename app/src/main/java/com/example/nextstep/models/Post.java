package com.example.nextstep.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public abstract class Post implements Parcelable {
    private String postId;
    private String userId;

    public Post(String userId) {
        this.userId = userId;
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

    protected Post(Parcel in){
        postId = in.readString();
        userId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(postId);
        dest.writeString(userId);
    }


}
