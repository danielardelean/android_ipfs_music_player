package com.example.textile;

import java.util.ArrayList;

public class Playlist {
    private String playlistName;
    private ArrayList<List> playlists;


    public Playlist() {
    }

    public Playlist(String playlistName, ArrayList<List> playlists) {
        this.playlistName = playlistName;
        this.playlists = playlists;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public ArrayList<List> getPlaylists() {
        return playlists;
    }
}

class List {
    private String mNameList;
    private ArrayList<Song> songArrayList;

    public List(String mNameList) {
        this.mNameList = mNameList;
    }

    public List(String mNameList, ArrayList<Song> songArrayList) {
        this.mNameList = mNameList;
        this.songArrayList = songArrayList;
    }

    public String getmNameList() {
        return mNameList;
    }

    public ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }

    public void addSong(Song song) {
        this.songArrayList.add(song);
    }
}
