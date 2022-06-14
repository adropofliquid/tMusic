package com.adropofliquid.tmusic.mediastore;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.adropofliquid.tmusic.items.AlbumItem;
import com.adropofliquid.tmusic.items.ArtistItem;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.views.activity.MainActivity;

import java.util.ArrayList;

public class LoadMediaStore {

    private final Context context;

    public LoadMediaStore(Context context){
        this.context = context;

    }

    private String[] songProjection(){

        return new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST_ID
        };
    }

    public ArrayList<SongItem> getAllSongs(){
        ArrayList<SongItem> songList = new ArrayList<>();

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        ContentResolver contentResolver = context.getContentResolver();
        Uri songLink = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songLink, songProjection(), null, null, sortOrder);

        if (songCursor != null && songCursor.moveToFirst())
        {
            int id = -1;
            int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songArtistId = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumId = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);


            do {
                id = id + 1;
                int disId = songCursor.getInt(songId);
                String disTitle = songCursor.getString(songTitle);
                String disArtist = songCursor.getString(songArtist);
                long disArtistId = songCursor.getLong(songArtistId);
                String disAlbum = songCursor.getString(songAlbum);
                long disAlbumId = songCursor.getLong(albumId);
                int disDuration = songCursor.getInt(songDuration);
                songList.add(new SongItem(id, disTitle, disArtist,disAlbum,
                        disAlbumId, SongItem.TYPE_SONG,disDuration, disArtistId, disId, id));

            }
            while (songCursor.moveToNext());
        }
        if (songCursor != null) {
            songCursor.close();
        }
        return songList;
    }

    public ArrayList<SongItem> getSongs(int type, long pid){

//        Log.v("sumtin entered", pid+"");

        ArrayList<SongItem> songList = new ArrayList<>();

        String selection;

        if(type == MainActivity.ALBUM_SONGS_LIST_VIEW)
            selection = MediaStore.Audio.Media.ALBUM_ID + " = ?";
        else
            selection = MediaStore.Audio.Media.ARTIST_ID + " = ?";

        String[] selectionArgs = new String[]{String.valueOf(pid)};

        String sortOrder = MediaStore.Audio.Media.TRACK + " ASC";

        ContentResolver contentResolver = context.getContentResolver();
        Uri songLink = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songLink, songProjection(), selection, selectionArgs, sortOrder);

        if (songCursor != null && songCursor.moveToFirst())
        {
            int id = -1;
            int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songArtistId = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumIdArt = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);


            do {
                id = id + 1;
                int disId = songCursor.getInt(songId);
                String disTitle = songCursor.getString(songTitle);
                String disArtist = songCursor.getString(songArtist);
                long disArtistId = songCursor.getLong(songArtistId);
                String disAlbum = songCursor.getString(songAlbum);
                long disAlbumId = songCursor.getLong(albumIdArt);
                int disDuration = songCursor.getInt(songDuration);
                Log.v("Added", disTitle);
                songList.add(new SongItem(id, disTitle, disArtist,disAlbum,
                        disAlbumId, SongItem.TYPE_SONG,disDuration, disArtistId, disId, id));

            }
            while (songCursor.moveToNext());
        }
        if (songCursor != null) {
            songCursor.close();
        }
        return songList;
    }

    public ArrayList<AlbumItem> getAllAlbums() {
        ArrayList<AlbumItem> albumList = new ArrayList<>();

        String[] projection = new String[] {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.FIRST_YEAR
        };

        String sortOrder = MediaStore.Audio.Albums.ALBUM + " ASC";

        ContentResolver contentResolver = context.getContentResolver();
        Uri link = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI; //audio DB

        Cursor cursor = contentResolver.query(link, projection, null, null, sortOrder);

        if (cursor != null && cursor.moveToFirst())
        {
            int album = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
            int album_id = cursor.getColumnIndex(MediaStore.Audio.Albums._ID);
            int year = cursor.getColumnIndex(MediaStore.Audio.Albums.FIRST_YEAR);

            do {
                String disArtist = cursor.getString(artist);
                String disAlbum = cursor.getString(album);
                long disId = cursor.getLong(album_id);
                int disYear = cursor.getInt(year);

                albumList.add(new AlbumItem(disId, disAlbum, disArtist, disYear));
            }
            while (cursor.moveToNext());
        }
        assert cursor != null;
        cursor.close();
        return albumList;
    }

    public ArrayList<AlbumItem> getAlbums(long artistId) {

//        System.out.println(artistId);
        ArrayList<AlbumItem> albumList = new ArrayList<>();

        String selection = MediaStore.Audio.Media.ARTIST_ID + " = ?";

        String[] selectionArgs = new String[]{String.valueOf(artistId)};

        String[] projection = new String[] {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.LAST_YEAR
        };

        String sortOrder = MediaStore.Audio.Albums.ARTIST + " ASC";

        ContentResolver contentResolver = context.getContentResolver();
        Uri link = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI; //audio DB

        Cursor cursor = contentResolver.query(link, projection, selection, selectionArgs, sortOrder);

        if (cursor != null && cursor.moveToFirst())
        {
            int album = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
            int album_id = cursor.getColumnIndex(MediaStore.Audio.Albums._ID);
            int year = cursor.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR);

            do {
                String disArtist = cursor.getString(artist);
                String disAlbum = cursor.getString(album);
                long disId = cursor.getLong(album_id);
                int disYear = cursor.getInt(year);

                albumList.add(new AlbumItem(disId, disAlbum, disArtist, disYear));
            }
            while (cursor.moveToNext());
        }
        assert cursor != null;
        cursor.close();
        return albumList;
    }

    public ArrayList<ArtistItem> getAllArtists() {
        ArrayList<ArtistItem> artistItems = new ArrayList<>();

        String[] projection = new String[] {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        };

        String sortOrder = MediaStore.Audio.Artists.ARTIST + " ASC";

        ContentResolver contentResolver = context.getContentResolver();
        Uri link = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI; //audio DB

        Cursor cursor = contentResolver.query(link, projection, null, null, sortOrder);

        if (cursor != null && cursor.moveToFirst())
        {
            int id = cursor.getColumnIndex(MediaStore.Audio.Artists._ID);
            int artist = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            int num_tracks = cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);

            do {

                long disId = cursor.getLong(id);
                String disArtist = cursor.getString(artist);
                long disNum = cursor.getLong(num_tracks);

                artistItems.add(new ArtistItem(disId,disArtist, disNum));
            }
            while (cursor.moveToNext());
        }
        assert cursor != null;
        cursor.close();
        return artistItems;
    }

}
