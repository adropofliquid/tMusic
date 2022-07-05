package com.adropofliquid.tmusic.data.queue;

import android.content.Context;
import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.data.SongRepository;
import com.adropofliquid.tmusic.room.dao.LastPlatedStateDao;
import com.adropofliquid.tmusic.room.model.LastPlayedStateItem;
import com.adropofliquid.tmusic.uncat.items.SongItem;

import java.util.List;

public class Queue {

    private final LastPlatedStateDao lastPlatedStateDao;
    private SongItem currentSong;
    private LastPlayedStateItem lastPlayedStateItem;

    private SongRepository songRepository;

    public Queue(Context context){
        this.lastPlatedStateDao = ((App)context.getApplicationContext()).getQueueDb().lastPlatedStateDao();
    }

    public void setSongRepository(SongRepository songRepository){
        this.songRepository = songRepository;
    }
    public void setCurrentSongWithId(int currentSongId) {
        currentSong = songRepository.getSongById(currentSongId);
    }

    public SongItem getCurrentSong(){
        return currentSong;
    }

    public boolean hasNext(boolean isShuffling){
        if(isShuffling)
            return hasNextOnPlayOrder();
        else
            return currentSong.getId() + 1 < songRepository.count();
    }

    public boolean hasNextOnPlayOrder(){
        return currentSong.getPlayOrder() + 1 < songRepository.count();
    }

    public boolean hasPrev(boolean isShuffling){
        if(isShuffling)
            return hasPrevOnPlayOrder();
        else
            return currentSong.getId() != 0;
    }

    public boolean hasPrevOnPlayOrder(){
        return currentSong.getPlayOrder() != 0;
    }

    public void saveLastPlayedState(long timePlayed, boolean getShuffled, int getRepeat){
        lastPlayedStateItem = new LastPlayedStateItem(
                currentSong.getId(), currentSong.getSongId(),
                timePlayed, getShuffled, getRepeat);

            lastPlatedStateDao.delete();
            lastPlatedStateDao.insertAll(lastPlayedStateItem);
    }

    public void loadLastPlayedState() {
        lastPlayedStateItem = lastPlatedStateDao.getLastPlayed();
    }

    public LastPlayedStateItem getLastPlayedState() {
        return lastPlayedStateItem;
    }

    public void setCurrentSong(SongItem song) {
        currentSong = song;
    }
}
