package com.adropofliquid.tmusic.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.adropofliquid.tmusic.room.dao.CacheVersionDao;
import com.adropofliquid.tmusic.room.dao.LastPlatedStateDao;
import com.adropofliquid.tmusic.room.model.CacheVersionItem;
import com.adropofliquid.tmusic.room.model.LastPlayedStateItem;
import com.adropofliquid.tmusic.uncat.items.SongItem;
import com.adropofliquid.tmusic.room.dao.SongDao;

@Database(entities = {SongItem.class, CacheVersionItem.class, LastPlayedStateItem.class}, version = 3)
public abstract class SongRoom extends RoomDatabase {

    public abstract SongDao songDao();

    public abstract CacheVersionDao cacheVersionDao();

    public abstract LastPlatedStateDao lastPlatedStateDao();


}