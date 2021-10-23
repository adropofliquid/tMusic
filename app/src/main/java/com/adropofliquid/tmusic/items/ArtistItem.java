package com.adropofliquid.tmusic.items;

public class ArtistItem {
    private String artist;
    private long tracks;

    public ArtistItem(String artist, long tracks)
    {
        this.tracks = tracks;
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public long getTracks() {
        return tracks;
    }
}
