package com.adropofliquid.tmusic.items;

import android.content.ContentUris;
import android.net.Uri;

public class SongItem {
    private String title;
    private String artist;
    private String album;
    private long albumId;
    private Uri songUri;
    private int duration;
    private boolean checked; //for Playlist song Picker
    private int songType;
    public static final int TYPE_SONG = 1; // actual song view
    public static final int TYPE_SHUFFLE = 2; //  shuffle view



    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public SongItem(int songType) {
        this.songType = songType;
    }
    public SongItem(String title, String artist,String album, long albumId,int duration, String data, int songType) {
        this.albumId = albumId;
        this.artist = artist;
        this.album = album;
        this.title = title;
        this.duration = duration;
        this.songUri = Uri.parse("file:///"+data);
        this.songType = songType;
    }
    public long getAlbumId() {
        return albumId;
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
    public Uri getAlbumArt() {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
        return uri;
    }
    public Uri getSongUri(){
        return songUri;
        //TODO
        //Sort for playing songs with weird names
        //Like Car #85
    }
    public int getDuration() {
        return duration;
    }
    public String getAlbum() {
        return album;
    }
}
