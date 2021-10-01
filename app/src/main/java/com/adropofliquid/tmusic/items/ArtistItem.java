package com.adropofliquid.tmusic.items;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistItem implements Parcelable {
    private String artist;
    private long tracks;

    public ArtistItem(String artist, long tracks)
    {
        this.tracks = tracks;
        this.artist = artist;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(artist);
        parcel.writeLong(tracks);
    }

    protected ArtistItem(Parcel in) {
        artist = in.readString();
        tracks = in.readLong();
    }

    public static final Creator<ArtistItem> CREATOR = new Creator<ArtistItem>() {
        @Override
        public ArtistItem createFromParcel(Parcel in) {
            return new ArtistItem(in);
        }

        @Override
        public ArtistItem[] newArray(int size) {
            return new ArtistItem[size];
        }
    };

    public String getArtist() {
        return artist;
    }

    public long getTracks() {
        return tracks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
