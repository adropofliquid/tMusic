package com.adropofliquid.tmusic.uncat.items;

import android.content.ContentUris;
import android.net.Uri;

import androidx.room.Ignore;

public class AlbumHeaderView{


    private String title;

    private String artist;

    private String album;

    private int count;

    private long albumId;

    @Ignore
    public AlbumHeaderView(int albumId, String artist, String album,
                           int count) {
        this.albumId = albumId;
        this.artist = artist;
        this.album = album;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Uri getAlbumArtUri() {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri, albumId);
    }

    public String getAlbum() {
        return album;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getCount() {
        return count;
    }
}
