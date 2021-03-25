package com.adropofliquid.tmusic.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.adropofliquid.tmusic.items.SongItem;
import java.util.ArrayList;

public class LoadMediaStore {

    private final Context context;

    public LoadMediaStore(Context context){
        this.context = context;
    }

    public ArrayList<SongItem> getAllSongs(){
        ArrayList<SongItem> songList = new ArrayList<>();

        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST_ID
        };

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        ContentResolver contentResolver = context.getContentResolver();
        Uri songLink = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songLink, projection, null, null, sortOrder);
        if (songCursor != null && songCursor.moveToFirst())
        {
            int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songArtistId = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumIdArt = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int position = -1;

            do {
                int disId = songCursor.getInt(songId);
                String disTitle = songCursor.getString(songTitle);
                String disArtist = songCursor.getString(songArtist);
                long disArtistId = songCursor.getLong(songArtistId);
                String disAlbum = songCursor.getString(songAlbum);
                long disAlbumId = songCursor.getLong(albumIdArt);
                int disDuration = songCursor.getInt(songDuration);

//                Log.d("Add: ",disTitle);

                songList.add(new SongItem(disId, disTitle, disArtist,disAlbum,
                        disAlbumId, SongItem.TYPE_SONG,disDuration, disArtistId, position+1));
                position = position+1;
            }
            while (songCursor.moveToNext());
        }
        if (songCursor != null) {
            songCursor.close();
        }
        return songList;
    }

}
