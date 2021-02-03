package com.adropofliquid.tmusic.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.adropofliquid.tmusic.items.SongItem;
import java.util.ArrayList;

public class LoadMediaStore {

    private Context context;

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
                MediaStore.Audio.Media.DATA
        };

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        ContentResolver contentResolver = context.getContentResolver();
        Uri songLink = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songLink, projection, null, null, sortOrder);
        if (songCursor != null && songCursor.moveToFirst())
        {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumIdArt = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA); //handle file I/O

            do {
                String disTitle = songCursor.getString(songTitle);
                String disArtist = songCursor.getString(songArtist);
                String disAlbum = songCursor.getString(songAlbum);
                String disData = songCursor.getString(songData);
                long disAlbumId = songCursor.getLong(albumIdArt);

                songList.add(new SongItem(disTitle, disArtist,disAlbum,
                        disAlbumId,disData, SongItem.TYPE_SONG));
            }
            while (songCursor.moveToNext());
        }
        return songList;
    }

}
