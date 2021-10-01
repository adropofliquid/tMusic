package com.adropofliquid.tmusic.db.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.adropofliquid.tmusic.items.SongItem;

@Database(entities = {SongItem.class}, version = 1)
public abstract class QueueDb extends RoomDatabase {

    public abstract QueueDao queueDao();
}