package com.sonu.musicplayer.model;

public class Playlist {
    private String playListId, playListName;

    @Override
    public String toString() {
        return "Playlist{" +
                "playListId='" + playListId + '\'' +
                ", playListName='" + playListName + '\'' +
                '}';
    }

    public Playlist(String playListId, String playListName) {
        this.playListId = playListId;
        this.playListName = playListName;
    }

    public String getPlayListId() {
        return playListId;
    }

    public void setPlayListId(String playListId) {
        this.playListId = playListId;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }
}
