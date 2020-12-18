package com.adropofliquid.tmusic.items;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class SongItem implements Parcelable {
    private final String title;



    private int duration;
    private String artist;
    private long albumId;
    private String album;
    private String data;
//    private Uri songUri;

    private final int songType;
    public static final int TYPE_SONG = 1; // actual song view
    public static final int TYPE_SHUFFLE = 2; //  shuffle view


    public SongItem(int songType) {
        this.songType = songType;
        this.title = "0";
    }

    public SongItem(String title, String artist,String album, long albumId,
                    int duration, String data, int songType) {
        this.albumId = albumId;
        this.artist = artist;
        this.title = title;
        this.songType = songType;
        this.data = data;
        this.album = album;
        this.duration = duration;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeInt(duration);
        parcel.writeString(artist);
        parcel.writeLong(albumId);
        parcel.writeString(album);
        parcel.writeString(data);
        parcel.writeInt(songType);
    }

    protected SongItem(Parcel in) {
        title = in.readString();
        duration = in.readInt();
        artist = in.readString();
        albumId = in.readLong();
        album = in.readString();
        data = in.readString();
        songType = in.readInt();
    }

    public static final Creator<SongItem> CREATOR = new Creator<SongItem>() {
        @Override
        public SongItem createFromParcel(Parcel in) {
            return new SongItem(in);
        }

        @Override
        public SongItem[] newArray(int size) {
            return new SongItem[size];
        }
    };

    public long getSongType() {
        return songType;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public Uri getAlbumArtUri() {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
        return uri;
    }

    public int getDuration() {
        return duration;
    }

    public Uri getSongUri(){
        return Uri.parse("file:///"+data);
        //TODO
        //Sort for playing songs with weird names
        //Like Car #85
    }

    public String getAlbum() {
        return album;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
