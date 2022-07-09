package com.adropofliquid.tmusic.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.room.SongRoom;
import com.adropofliquid.tmusic.room.dao.CacheVersionDao;
import com.adropofliquid.tmusic.room.dao.LastPlatedStateDao;
import com.adropofliquid.tmusic.room.dao.SongDao;
import com.adropofliquid.tmusic.room.model.CacheVersionItem;
import com.adropofliquid.tmusic.room.model.LastPlayedStateItem;
import com.adropofliquid.tmusic.uncat.items.SongItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class SongRepository {

    private static final String TAG = "Song Repository: ";
    private final Executor executor;
    private final Context context;
    private final SongRoom songRoom;

    public SongRepository(Context context){
        this.executor =  ((App)context.getApplicationContext()).getExecutor();
        this.context = context;
        this.songRoom = ((App)context.getApplicationContext()).getSongRoom();
    }

    public void loadSongs(OnSongsLoadedCallback onSongsLoadedCallback) {
        executor.execute(()->{
            List<SongItem> songs;

                songs = getCachedSongs(context);
                Log.d(TAG, "Songs loaded from cache: " + songs.size());
                onSongsLoadedCallback.onLoaded(songs);
                if(!getCacheVersion(context).equals(MediaStore.getVersion(context)))
                    updateCache(context, onSongsLoadedCallback);
        });
    }

    public void loadCacheSongsPlayOrder(OnSongsLoadedCallback onSongsLoadedCallback) {
        executor.execute(()->{
            List<SongItem> songs = getCachedSongsPlayOrder(context);
            Log.d(TAG, "Songs loaded from cache: " + songs.size());
            onSongsLoadedCallback.onLoaded(songs);

            for (int i = 0;i < songs.size(); ++i) {
                Log.d("Song", songs.get(i).getId()+" "+songs.get(i).getPlayOrder());
            }
        });

    }

    private List<SongItem> getCachedSongsPlayOrder(Context context) {
        SongRoom songRoom = ((App)context.getApplicationContext()).getSongRoom();
        SongDao songDao = songRoom.songDao();
        return songDao.getAllByPlayOrder();
    }

    public void shuffleSongs(OnSongsShuffledCallback onSongsShuffledCallback) {
        executor.execute(()->{
            List<SongItem> songs = getCachedSongs(context);
            List<Integer> shuffledPlayOrder = shuffledPlayOrder(songs.size());

            for(int i = 0; i < shuffledPlayOrder.size(); i++){
                songs.get(i).setPlayOrder(shuffledPlayOrder.get(i));
            }

            cacheSongs(context,songs, getCacheVersion(context));
            onSongsShuffledCallback.onShuffled(getSongByPlayOrder(0));
        });
    }

    public SongItem getSongByPlayOrder(int playOrder) {
        return songRoom.songDao().findByPlayOrder(playOrder);
    }

    private List<Integer> shuffledPlayOrder(int size){
        List<Integer> playOrder = new ArrayList<>();
        for(int i = 0; i < size; i++){
            playOrder.add(i);
        }
        Collections.shuffle(playOrder);
        return playOrder;
    }

    public void updateCache(Context context, OnSongsLoadedCallback onSongsLoadedCallback) {
        executor.execute(() -> {
            List<SongItem> mediaStoreSongs = getAllSongsFromMediaStore(context);
            if(!mediaStoreSongs.isEmpty())
                cacheSongs(context,mediaStoreSongs, MediaStore.getVersion(context));
            else
                clearCache(context);
            loadSongs(onSongsLoadedCallback); // to update whoever initiated dis
        });
    }

    private void clearCache(Context context) {

        SongRoom songRoom = ((App)context.getApplicationContext()).getSongRoom();
        SongDao songDao = songRoom.songDao();

        songRoom.runInTransaction(() -> {
            songDao.deleteAll();
            Log.d(TAG, "Cache cleared: ");
        });

        updateCacheVersion(String.valueOf(0), context);
    }

    private void cacheSongs(Context context, List<SongItem> songs, String cacheVersion) {

        SongRoom songRoom = ((App)context.getApplicationContext()).getSongRoom();
        SongDao songDao = songRoom.songDao();

        songRoom.runInTransaction(() -> {
            songDao.deleteAll();
            songDao.insertList(songs);
            Log.d(TAG, "Songs cached: " + songs.size());

        });
        updateCacheVersion(cacheVersion, context);
    }

    private void updateCacheVersion(String version, Context context) {
        CacheVersionItem cacheVersionItem = new CacheVersionItem();
        cacheVersionItem.setCacheVersion(version);
        cacheVersionItem.setId(1);

        SongRoom songRoom = ((App)context.getApplicationContext()).getSongRoom();
        CacheVersionDao cacheVersionDao = songRoom.cacheVersionDao();

        songRoom.runInTransaction(() -> {
            cacheVersionDao.delete();
            cacheVersionDao.insertAll(cacheVersionItem);
        });

    }

    private String getCacheVersion(Context context){

        SongRoom songRoom = ((App)context.getApplicationContext()).getSongRoom();
        CacheVersionDao cacheVersionDao = songRoom.cacheVersionDao();

        return cacheVersionDao.getCacheVersion() == null ? String.valueOf(0) : cacheVersionDao.getCacheVersion().getCacheVersion();
    }

    private List<SongItem> getCachedSongs(Context context) {

        SongRoom songRoom = ((App)context.getApplicationContext()).getSongRoom();
        SongDao songDao = songRoom.songDao();
        return songDao.getAll();
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

    public SongItem getSong(int position){
        SongDao songDao = songRoom.songDao();
        return songDao.findById(position);
    }

    public int count() {
        SongDao songDao = songRoom.songDao();
        return songDao.count();
    }

    public SongItem getSongById(int currentSongId) {
        return songRoom.songDao().findById(currentSongId);
    }

    public interface OnSongsLoadedCallback{
        void onLoaded(List<SongItem> songs);
    }

    public interface OnSongsShuffledCallback{
        void onShuffled(SongItem song);
    }

    public interface OnLastSongLoadCallback{
        void onLoad(LastPlayedStateItem last, SongItem song);
    }

    public LastPlayedStateItem getLastPlayedState() {
        LastPlatedStateDao lastPlatedStateDao = songRoom.lastPlatedStateDao();
        return lastPlatedStateDao.getLastPlayed();
    }

    public void loadLastState(OnLastSongLoadCallback onLastSongLoadCallback) {
        executor.execute(()->{
            onLastSongLoadCallback.onLoad(getLastPlayedState(), getSong(getLastPlayedState().getId()));
        });
    }

    public void saveLastPlayedState(LastPlayedStateItem lastPlayedStateItem){
        LastPlatedStateDao lastPlatedStateDao = songRoom.lastPlatedStateDao();
        lastPlatedStateDao.delete();
        lastPlatedStateDao.insertAll(lastPlayedStateItem);
    }
}
