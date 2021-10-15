package com.adropofliquid.tmusic.items;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class SongItem{

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "album")
    private String album;

    @ColumnInfo(name = "duration")
    private int duration;

    @ColumnInfo(name = "songId")
    private int songId;

    @ColumnInfo(name = "playOrder")
    private int playOrder;

    private long albumId;
    private long artistId;
    private int songType;
    public static final int TYPE_SONG = 1; // actual song view
    public static final int TYPE_SHUFFLE = 2; //  shuffle view

    public SongItem(){}

    @Ignore
    public SongItem(int songType) {
        this.songType = songType;
        this.title = "0";
    }

    @Ignore
    public SongItem(int id, String title, String artist,String album,
                    long albumId, int songType, int duration, long artistId, int songId, int playOrder) {
        this.id = id;
        this.albumId = albumId;
        this.artist = artist;
        this.title = title;
        this.songType = songType;
        this.album = album;
        this.duration = duration;
        this.artistId = artistId;
        this.songId = songId;
        this.playOrder = playOrder;
    }

    public int getSongId(){
        return songId;
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
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
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

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public void setSongType(int songType) {
        this.songType = songType;
    }

    public int getSongType(){
        return songType;
    }

    public static int getTypeSong() {
        return TYPE_SONG;
    }

    public static int getTypeShuffle() {
        return TYPE_SHUFFLE;
    }

    public int getPlayOrder() {
        return playOrder;
    }

    public void setPlayOrder(int playOrder) {
        this.playOrder = playOrder;
    }


}
