package com.example.textile;

import java.util.ArrayList;

public class Album {
    private String mAlbumName;
    private ArrayList<Song> mAlbumSongsArraylist;

    public Album() {
    }

    public Album(String mAlbumName) {
        this.mAlbumName = mAlbumName;
        this.mAlbumSongsArraylist = new ArrayList<>();
    }

    public String getmAlbumName() {
        return mAlbumName;
    }

    public ArrayList<Song> getmAlbumSongsArraylist() {
        return mAlbumSongsArraylist;
    }

    public void addSong(Song song) {
        this.mAlbumSongsArraylist.add(song);
    }

    @Override
    public String toString() {
        return "Album{" +
                "mAlbumName='" + mAlbumName + '\'' +
                ", mAlbumSongsArraylist=" + mAlbumSongsArraylist +
                '}';
    }
}
