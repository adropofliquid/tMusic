package com.adropofliquid.tmusic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlayQueueDb extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 11;
    public static final String DATABASE_NAME = "PlayQueue.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedContract.PlayQueue.TABLE_NAME + " (" +
                    FeedContract.PlayQueue._ID + " INTEGER PRIMARY KEY ," +
                    FeedContract.PlayQueue.COLUMN_NAME_SONG_ID + " INTEGER," +
                    FeedContract.PlayQueue.COLUMN_NAME_ALBUM + " TEXT," +
                    FeedContract.PlayQueue.COLUMN_NAME_ARTIST + " TEXT," +
                    FeedContract.PlayQueue.COLUMN_NAME_DURATION + " LONG," +
                    FeedContract.PlayQueue.COLUMN_NAME_PLAY_ORDER + " INTEGER," +
                    FeedContract.PlayQueue.COLUMN_NAME_PLAY_ORDER_BCK + " INTEGER," +
                    FeedContract.PlayQueue.COLUMN_NAME_TITLE + " TEXT," +
                    FeedContract.PlayQueue.COLUMN_NAME_URI + " TEXT," +
                    FeedContract.PlayQueue.COLUMN_NAME_ALBUM_ID + " LONG," +
                    FeedContract.PlayQueue.COLUMN_NAME_ARTIST_ID + " LONG)";

    private static final String SQL_CREATE_ENTRIES2 =
            "CREATE TABLE " + FeedContract.LastPlayed.TABLE_NAME + " (" +
                    FeedContract.LastPlayed._ID + " INTEGER PRIMARY KEY," +
                    FeedContract.LastPlayed.COLUMN_NAME_QUEUE_POSITION + " INTEGER," +
                    FeedContract.LastPlayed.COLUMN_NAME_PLAY_POSITION + " INTEGER)";

    private static final String SQL_DELETE_ENTRY = "DROP TABLE IF EXISTS " +
            FeedContract.PlayQueue.TABLE_NAME;
    private static final String SQL_DELETE_ENTRY2 = "DROP TABLE IF EXISTS " +
            FeedContract.LastPlayed.TABLE_NAME;

    public PlayQueueDb(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES2);

        ContentValues values = new ContentValues();
        values.put(FeedContract.LastPlayed._ID,0);
        values.put(FeedContract.LastPlayed.COLUMN_NAME_QUEUE_POSITION, 0);
        values.put(FeedContract.LastPlayed.COLUMN_NAME_PLAY_POSITION, 0);
        db.insert(FeedContract.LastPlayed.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRY);
        db.execSQL(SQL_DELETE_ENTRY2);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
