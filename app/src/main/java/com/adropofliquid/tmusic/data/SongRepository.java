package com.adropofliquid.tmusic.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.room.SongRoom;
import com.adropofliquid.tmusic.room.dao.SongDao;
import com.adropofliquid.tmusic.uncat.items.SongItem;
import com.adropofliquid.tmusic.data.mediastore.LoadMediaStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class SongRepository {

    private static final String TAG = "Song Repository: ";
    private final Executor executor;

    public SongRepository(Executor executor){
        this.executor = executor;
    }

    public SongRepository(Context context){
        this.executor =  ((App)context.getApplicationContext()).getExecutor();
    }

    public void loadSongs(Context context, OnSongsLoadedCallback onSongsLoadedCallback) {
        executor.execute(()->{
            List<SongItem> songs;
            if(!songCacheIsLatest()) {
                songs = new LoadMediaStore(context).getAllSongs();
                onSongsLoadedCallback.onLoaded(songs);
                cacheSongs(context,songs);
            }
            else {
                songs = getCachedSongs(context);
                Log.d(TAG, "Songs loaded from cache: " + songs.size());
                onSongsLoadedCallback.onLoaded(songs);
            }
        });
    }

    public void updateCache(Context context) {
        executor.execute(() -> {
            List<SongItem> mediaStoreSongs = getAllSongsFromMediaStore(context);
            if(!mediaStoreSongs.isEmpty())
                cacheSongs(context,mediaStoreSongs);
        });
    }

    private void cacheSongs(Context context, List<SongItem> songs) {

        SongRoom songRoom = ((App)context.getApplicationContext()).getSongRoom();
        SongDao songDao = songRoom.songDao();

        songRoom.runInTransaction(() -> {
            songDao.deleteAll();
            songDao.insertList(songs);
            Log.d(TAG, "Songs cached: " + songs.size());

        });
    }

    private boolean songCacheIsLatest() {
        //TODO check if the cache has current song already
        // return false jus for testing
        return true;
    }

    private List<SongItem> getCachedSongs(Context context) {

        SongRoom songRoom = ((App)context.getApplicationContext()).getSongRoom();
        SongDao songDao = songRoom.songDao();
        return songDao.getAll();
    }

    private void loadMediaStoreToSongRoom(Context context){
        if(!songCacheIsLatest()) {
            List<SongItem> mediaStoreSongs = getAllSongsFromMediaStore(context);
            List<SongItem> cacheSongs = getCachedSongs(context);
        }
    }

    private List<SongItem> getAllSongsFromMediaStore(Context context){

        String[] songProjection = new String[] {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ARTIST_ID
            };

        List<SongItem> songList = new ArrayList<>();

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        ContentResolver contentResolver = context.getContentResolver();
        Uri songLink = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursor = contentResolver.query(songLink, songProjection, null, null, sortOrder);

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

        Log.d(TAG, "Songs loaded from store: " + songList.size());
        return songList;
    }

    public SongItem getSong(Context context,int position){
        SongRoom songRoom = ((App)context.getApplicationContext()).getSongRoom();
        SongDao songDao = songRoom.songDao();
        return songDao.findById(position);
    }

    public void putRoomSongsInQueue() {

    }

    public interface OnSongsLoadedCallback{
        void onLoaded(List<SongItem> songs);
    }

}
