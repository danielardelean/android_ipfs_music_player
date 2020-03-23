package com.example.textile;

import java.util.ArrayList;

public class Artist {
    private ArrayList<Album> albumArrayList;
    private String artistName;

    public Artist() {
    }

    public Artist(String artistName) {
        this.artistName = artistName;
        this.albumArrayList = new ArrayList<>();
    }

    public ArrayList<Album> getAlbumArrayList() {
        return albumArrayList;
    }

    public String getArtistName() {
        return artistName;
    }

    public void addAlbum(Album album) {
        this.albumArrayList.add(album);
    }

    @Override
    public String toString() {
        return "Artist{" +
                "albumArrayList=" + albumArrayList +
                ", artistName='" + artistName + '\'' +
                '}';
    }
}
