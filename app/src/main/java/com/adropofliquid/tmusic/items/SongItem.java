package com.adropofliquid.tmusic.items;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class SongItem implements Parcelable {
    private int id;
    private final String title;
    private String artist;
    private long albumId;
    private long artistId;
    private String album;
    private int duration;
    private int position;

    private final int songType;
    public static final int TYPE_SONG = 1; // actual song view
    public static final int TYPE_SHUFFLE = 2; //  shuffle view


    public SongItem(int songType) {
        this.songType = songType;
        this.title = "0";
    }

    public SongItem(int id, String title, String artist,String album,
                    long albumId, int songType, int duration, long artistId, int position) {
        this.id = id;
        this.albumId = albumId;
        this.artist = artist;
        this.title = title;
        this.songType = songType;
        this.album = album;
        this.duration = duration;
        this.artistId = artistId;
        this.position = position;
    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeLong(albumId);
        parcel.writeString(album);
        parcel.writeInt(songType);
        parcel.writeInt(duration);
        parcel.writeLong(artistId);
        parcel.writeInt(position);
    }

    protected SongItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        artist = in.readString();
        albumId = in.readLong();
        album = in.readString();
        songType = in.readInt();
        duration = in.readInt();
        artistId = in.readLong();
        position = in.readInt();
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

    public int getPosition(){
        return position;
    }
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
        return ContentUris.withAppendedId(sArtworkUri, albumId);
    }

    public String getAlbum() {
        return album;
    }

    public Uri getUri(){
        return ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
    }
    public int getId(){
        return id;
    }
    public int getDuration(){
        return duration;
    }

    public long getAlbumId(){
        return albumId;
    }
    public long getArtistId(){
        return artistId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
