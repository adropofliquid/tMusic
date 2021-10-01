package com.adropofliquid.tmusic.items;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class AlbumItem implements Parcelable {

    private long id;
    private String name;
    private String artist;

    public AlbumItem(long id, String name, String artist)
    {
        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(artist);
    }

    protected AlbumItem(Parcel in) {
        id = in.readLong();
        name = in.readString();
        artist = in.readString();
    }

    public static final Creator<AlbumItem> CREATOR = new Creator<AlbumItem>() {
        @Override
        public AlbumItem createFromParcel(Parcel in) {
            return new AlbumItem(in);
        }

        @Override
        public AlbumItem[] newArray(int size) {
            return new AlbumItem[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

}
