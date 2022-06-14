package com.adropofliquid.tmusic.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.room.dao.SongDao;

@Database(entities = {SongItem.class}, version = 1)
public abstract class SongRoom extends RoomDatabase {

    public abstract SongDao songDao();
}