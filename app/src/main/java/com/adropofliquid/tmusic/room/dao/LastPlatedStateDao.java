package com.adropofliquid.tmusic.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.adropofliquid.tmusic.room.model.LastPlayedStateItem;

@Dao
public interface LastPlatedStateDao {

    @Query("SELECT * FROM lastplayedstateitem LIMIT 1")
    LastPlayedStateItem getLastPlayed();

    @Insert
    void insertAll(LastPlayedStateItem... lastPlayedStateItems);

    @Query("DELETE FROM lastplayedstateitem")
    void delete();

    @Query("SELECT shuffled FROM lastplayedstateitem LIMIT 1")
    boolean getShufflingMode();
}
