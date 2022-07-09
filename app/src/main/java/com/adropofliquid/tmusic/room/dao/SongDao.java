package com.adropofliquid.tmusic.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.adropofliquid.tmusic.uncat.items.ShuffleItem;
import com.adropofliquid.tmusic.uncat.items.SongItem;

import java.util.List;

@Dao
public interface SongDao {

/*    @Query("SELECT * FROM queue")
    List<Queue> getAll();

    @Query("SELECT * FROM queue WHERE id IN (:queueIds)")
    List<Queue> loadAllByIds(int[] queueIds);

    @Query("SELECT * FROM queue WHERE title LIKE :title LIMIT 1")
    Queue findByTitle(String title);

    @Query("SELECT * FROM queue WHERE position LIKE :position LIMIT 1")
    Queue findByPosition(int position);

    @Insert
    void insertAll(Queue... queues);

    @Delete
    void delete(Queue queue);*/

    @Query("SELECT * FROM songitem")
    List<SongItem> getAll();

    @Query("SELECT * FROM songitem WHERE id IN (:queueIds)")
    List<SongItem> loadAllByIds(int[] queueIds);

    @Query("SELECT * FROM songitem WHERE title LIKE :title LIMIT 1")
    SongItem findByTitle(String title);

    @Query("SELECT * FROM songitem WHERE id LIKE :id LIMIT 1")
    SongItem findById(int id);

    @Query("SELECT * FROM songitem WHERE playOrder LIKE :playOrder LIMIT 1")
    SongItem findByPlayOrder(int playOrder);

    @Insert
    void insertAll(SongItem... queues);

    @Insert
    void insertList(List<SongItem> songs);

    @Delete
    void delete(SongItem queue);

    @Query("DELETE FROM songitem")
    void deleteAll();

    @Query("SELECT count(*) FROM songitem")
    int count();

    @Query("SELECT id, playOrder FROM songitem")
    List<ShuffleItem> loadForShuffling();

    @Query("SELECT playOrder FROM songitem")
    List<Integer> loadForShuffle();

    @Query("UPDATE songitem SET playOrder = :playOrder WHERE id = :id")
    void loadForShuffle(int id, int playOrder);

    @Query("SELECT * FROM songitem ORDER BY playOrder ASC")
    List<SongItem> getAllByPlayOrder();
}
