package com.adropofliquid.tmusic.uncat.items;

public class ArtistItem {
    private String artist;
    private long tracks;
    private long id;

    public ArtistItem(long id, String artist, long tracks)
    {
        this.id = id;
        this.tracks = tracks;
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public long getTracks() {
        return tracks;
    }

    public long getId() {
        return id;
    }
}
