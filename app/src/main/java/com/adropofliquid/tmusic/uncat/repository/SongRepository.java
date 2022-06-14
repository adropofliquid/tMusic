package com.adropofliquid.tmusic.uncat.repository;

import android.content.Context;

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.uncat.items.SongItem;
import com.adropofliquid.tmusic.uncat.mediastore.LoadMediaStore;
import com.adropofliquid.tmusic.room.SongRoom;
import com.adropofliquid.tmusic.room.dao.SongDao;

import java.util.List;
import java.util.concurrent.Executor;

public class SongRepository {

    private final SongRoom songRoom;
    private final SongDao songDao;
    private final Context context;
    private final Executor executor;

    public SongRepository(Context context) {
        this.context = context;
        songRoom = ((App) context.getApplicationContext()).getSongRoom();
        executor = (((App) context.getApplicationContext()).getExecutor());
        songDao = songRoom.songDao();
    }

    public void makeRoomMatchMediaStore(){
        executor.execute(() -> {
            List<SongItem> songs = new LoadMediaStore(context).getAllSongs();
            songRoom.runInTransaction(() -> {
                songDao.deleteAll();
                songDao.insertList(songs);

            });
        });
    }

}
