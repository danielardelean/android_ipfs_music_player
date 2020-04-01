package com.example.textile;

import java.io.Serializable;

public class Song implements Serializable {
    private String mTitle;
    private String mName;
    private String mHash;
    private int mSkipTimes;
    private int mPlayingTimes;
    private String mGenre;
    private float weight;
    private boolean mLike;

    public Song() {}

    public Song(String mTitle, String mName, String mHash, String mGenre) {
        this.mTitle = mTitle;
        this.mName = mName;
        this.mHash = mHash;
        this.mGenre = mGenre;
    }

    //For streaming music history
    public Song(String mTitle, String mName, String mHash, int mSkipTimes, int mPlayingTimes, String mGenre, boolean like) {
        this.mTitle = mTitle;
        this.mName = mName;
        this.mHash = mHash;
        this.mSkipTimes = mSkipTimes;
        this.mPlayingTimes = mPlayingTimes;
        this.mGenre = mGenre;
        this.mLike = like;

        this.weight = 0;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmName() {
        return mName;
    }

    public String getmHash() {
        return mHash;
    }

    public int getmSkipTimes() {
        return mSkipTimes;
    }

    public int getmPlayingTimes() {
        return mPlayingTimes;
    }

    public String getmGenre() {
        return mGenre;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Song{" +
                "mTitle='" + mTitle + '\'' +
                ", mName='" + mName + '\'' +
                ", mHash='" + mHash + '\'' +
                ", mSkipTimes=" + mSkipTimes +
                ", mPlayingTimes=" + mPlayingTimes +
                ", mGenre='" + mGenre + '\'' +
                ", weight=" + weight +
                ", mLike=" + mLike +
                '}';
    }

    public void setmSkipTimes(int mSkipTimes) {
        this.mSkipTimes = mSkipTimes;
    }

    public void setmPlayingTimes(int mPlayingTimes) {
        this.mPlayingTimes = mPlayingTimes;
    }

    public boolean ismLike() {
        return mLike;
    }

    public void setmLike(boolean mLike) {
        this.mLike = mLike;
    }
}
