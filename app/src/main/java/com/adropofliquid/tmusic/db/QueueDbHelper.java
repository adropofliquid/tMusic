package com.adropofliquid.tmusic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.adropofliquid.tmusic.items.SongItem;

import java.util.ArrayList;

public class QueueDbHelper {

    private SQLiteDatabase queueDb;
    private Context context;

    public QueueDbHelper(Context context){

        this.context = context;
        PlayQueueDb dbHelper = new PlayQueueDb(context);
        queueDb = dbHelper.getWritableDatabase();
    }

    public void setQueue(ArrayList<SongItem> songList){

        // TODO
        //  empty database first
        if(!tableIsEmpty())
            emptyQueue();

        // Create a new map of values, where column names are the keys

        queueDb.beginTransaction();
        for(int i = 0;i < songList.size(); i++){
            ContentValues values = new ContentValues();
            values.put(FeedContract.PlayQueue._ID, songList.get(i).getId());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_ALBUM, songList.get(i).getAlbum());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_ARTIST, songList.get(i).getArtist());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_DURATION, songList.get(i).getDuration());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_PLAY_ORDER, i);
            values.put(FeedContract.PlayQueue.COLUMN_NAME_PLAY_ORDER_BCK, i);
            values.put(FeedContract.PlayQueue.COLUMN_NAME_TITLE, songList.get(i).getTitle());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_ALBUM_ID, songList.get(i).getAlbumId());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_ARTIST_ID, songList.get(i).getArtistId());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_URI, String.valueOf(songList.get(i).getUri()));

            queueDb.insert(FeedContract.PlayQueue.TABLE_NAME,
                    null, values);
        }
        queueDb.setTransactionSuccessful();
        queueDb.endTransaction();

        //FIXME fix for multiple taps
        // maybe add in the background thread

    }

    public void saveQueue(ArrayList<SongItem> songList, int position, int playPosition){

        // TODO
        //  empty database first
        if(!tableIsEmpty())
            emptyQueue();

        // Create a new map of values, where column names are the keys

        queueDb.beginTransaction();
        for(int i = 0;i < songList.size(); i++){
            ContentValues values = new ContentValues();
            values.put(FeedContract.PlayQueue._ID, i);
            values.put(FeedContract.PlayQueue.COLUMN_NAME_SONG_ID, songList.get(i).getId());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_ALBUM, songList.get(i).getAlbum());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_ARTIST, songList.get(i).getArtist());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_DURATION, songList.get(i).getDuration());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_PLAY_ORDER, i);
            values.put(FeedContract.PlayQueue.COLUMN_NAME_PLAY_ORDER_BCK, i);
            values.put(FeedContract.PlayQueue.COLUMN_NAME_TITLE, songList.get(i).getTitle());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_ALBUM_ID, songList.get(i).getAlbumId());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_ARTIST_ID, songList.get(i).getArtistId());
            values.put(FeedContract.PlayQueue.COLUMN_NAME_URI, String.valueOf(songList.get(i).getUri()));

            queueDb.insert(FeedContract.PlayQueue.TABLE_NAME,
                    null, values);
        }

        ContentValues value = new ContentValues();
        String selection = FeedContract.LastPlayed._ID + " LIKE ?";
        String[] selectionArgs = { "0" };

        value.put(FeedContract.LastPlayed.COLUMN_NAME_QUEUE_POSITION,position);
        value.put(FeedContract.LastPlayed.COLUMN_NAME_PLAY_POSITION, playPosition);
        queueDb.update(FeedContract.LastPlayed.TABLE_NAME, value, selection, selectionArgs);

        queueDb.setTransactionSuccessful();
        queueDb.endTransaction();

        close();
    }

    private boolean tableIsEmpty() {

        boolean empty = false;
        Cursor cursor = queueDb.rawQuery("SELECT COUNT(*) FROM "+ FeedContract.PlayQueue.TABLE_NAME,null);
        if(cursor != null){
            cursor.moveToFirst();
            empty = cursor.getInt(0) <= 0;
        }
        return empty;
    }

    public void emptyQueue(){
        queueDb.delete(FeedContract.PlayQueue.TABLE_NAME, null, null);
    }

    public ArrayList<SongItem> getSavedQueue(){
        ArrayList<SongItem> songList = new ArrayList<>();

        String sortOrder = FeedContract.PlayQueue._ID + " ASC";

        Cursor songCursor = queueDb.query(FeedContract.PlayQueue.TABLE_NAME, null, null, null,null,null, sortOrder);

        if (songCursor != null && songCursor.moveToFirst())
        {

            int songId = songCursor.getColumnIndex(FeedContract.PlayQueue.COLUMN_NAME_SONG_ID);
            int songTitle = songCursor.getColumnIndex(FeedContract.PlayQueue.COLUMN_NAME_TITLE);
            int songArtist = songCursor.getColumnIndex(FeedContract.PlayQueue.COLUMN_NAME_ARTIST);
            int songArtistId = songCursor.getColumnIndex(FeedContract.PlayQueue.COLUMN_NAME_ARTIST_ID);
            int songAlbum = songCursor.getColumnIndex(FeedContract.PlayQueue.COLUMN_NAME_ALBUM);
            int albumIdArt = songCursor.getColumnIndex(FeedContract.PlayQueue.COLUMN_NAME_ALBUM_ID);
            int songDuration = songCursor.getColumnIndex(FeedContract.PlayQueue.COLUMN_NAME_DURATION);


            do {
                int disId = songCursor.getInt(songId);
                String disTitle = songCursor.getString(songTitle);
                String disArtist = songCursor.getString(songArtist);
                long disArtistId = songCursor.getLong(songArtistId);
                String disAlbum = songCursor.getString(songAlbum);
                long disAlbumId = songCursor.getLong(albumIdArt);
                int disDuration = songCursor.getInt(songDuration);

                songList.add(new SongItem(disId, disTitle, disArtist,disAlbum,
                        disAlbumId, SongItem.TYPE_SONG,disDuration, disArtistId,0));
            }
            while (songCursor.moveToNext());
        }
        if (songCursor != null) {
            songCursor.close();
        }
        return songList;
    }

    public int[] getSavedPosition(){

        int [] positions = new int[]{0,0};
        //int songPosition = 0;


        Cursor songCursor = queueDb.query(FeedContract.LastPlayed.TABLE_NAME, null, null, null,null,null, null);

        if (songCursor != null && songCursor.moveToFirst())
        {
            int position = songCursor.getColumnIndex(FeedContract.LastPlayed.COLUMN_NAME_QUEUE_POSITION);
            int playPosition = songCursor.getColumnIndex(FeedContract.LastPlayed.COLUMN_NAME_PLAY_POSITION);

            do {
                positions[0] = songCursor.getInt(position);
                positions[1] = songCursor.getInt(playPosition);
            }
            while (songCursor.moveToNext());
        }
        if (songCursor != null) {
            songCursor.close();
        }
        return positions;
    }

    public void close(){
        queueDb.close();
    }
}