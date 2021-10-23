package com.adropofliquid.tmusic.items;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class AlbumItem {

    private long id;
    private int year;
    private String name;
    private String artist;

    public AlbumItem(){}
    public AlbumItem(long id, String name, String artist, int year)
    {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.year = year;
    }

    public long getId(){
        return id;
    }
    public String getArtist() {
        return artist;
    }

    public Uri getAlbumArtUri() {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri, id);
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }
}
