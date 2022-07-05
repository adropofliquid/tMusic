package com.adropofliquid.tmusic.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.adropofliquid.tmusic.room.model.CacheVersionItem;

@Dao
public interface CacheVersionDao {

    @Query("SELECT * FROM cacheversionitem LIMIT 1")
    CacheVersionItem getCacheVersion();

    @Insert
    void insertAll(CacheVersionItem... cacheVersionItems);

    @Query("DELETE FROM cacheversionitem")
    void delete();

}
