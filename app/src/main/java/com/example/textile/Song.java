package com.example.textile;

import java.io.Serializable;

public class Song implements Serializable {
    private String mTitle;
    private String mName;
    private String mHash;


    public Song(String mTitle, String mName, String mHash) {
        this.mTitle = mTitle;
        this.mName = mName;
        this.mHash = mHash;
    }

    public Song() { }

    public String getmTitle() {
        return mTitle;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmHash() {
        return mHash;
    }

    public void setmHash(String mHash) {
        this.mHash = mHash;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + mTitle + '\'' +
                ", artistName='" + mName + '\'' +
                ", hash='" + mHash + '\'' +
                '}';
    }
}
