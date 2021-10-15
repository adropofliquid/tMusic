package com.adropofliquid.tmusic.queue.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.adropofliquid.tmusic.items.LastPlayedStateItem;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.queue.room.LastPlatedStateDao;
import com.adropofliquid.tmusic.queue.room.QueueDao;

@Database(entities = {SongItem.class, LastPlayedStateItem.class}, version = 4)
public abstract class QueueDb extends RoomDatabase {

    public abstract QueueDao queueDao();

    public abstract LastPlatedStateDao lastPlatedStateDao();

}