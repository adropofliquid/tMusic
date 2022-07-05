package com.adropofliquid.tmusic.data.queue;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.room.dao.LastPlatedStateDao;
import com.adropofliquid.tmusic.room.dao.QueueDao;
import com.adropofliquid.tmusic.room.QueueDb;
import com.adropofliquid.tmusic.room.model.LastPlayedStateItem;
import com.adropofliquid.tmusic.uncat.items.SongItem;

import java.util.List;

public class Queue {

    private final QueueDao queueDao;
    private final LastPlatedStateDao lastPlatedStateDao;
    private final QueueDb queueDb;
    private SongItem currentSong;
    private LastPlayedStateItem lastPlayedStateItem;
    private Context context;
    private Activity activity;

    public Queue(Context context){
        this.queueDb = ((App)context.getApplicationContext()).getQueueDb(); //weird. Lol
        this.queueDao = queueDb.queueDao();
        this.lastPlatedStateDao = queueDb.lastPlatedStateDao();
        this.context = context;
    }

    public Queue(Activity activity){
        this.queueDb = ((App)activity.getApplicationContext()).getQueueDb();
        this.queueDao = queueDb.queueDao();
        this.lastPlatedStateDao = queueDb.lastPlatedStateDao();
        this.activity = activity;
    }

    public void setCurrentSongWithId(int currentSongId) {
        currentSong = queueDao.findById(currentSongId);
    }

    public void setCurrentSongWithPlayOrder(int currentSongId) {
        currentSong = queueDao.findByPlayOrder(currentSongId);
        Log.v("Play", currentSong.getTitle());
    }

    public SongItem getSong(int position){
        return queueDao.findById(position);
    }

    public SongItem getSongFromPlayOrder(int position){
        return queueDao.findByPlayOrder(position);
    }

    public SongItem getCurrentSong(){
        return currentSong;
    }

    public boolean hasNext(){
        return currentSong.getId() + 1 < queueDao.count();
    }

    public boolean hasNextOnPlayOrder(){
        return currentSong.getPlayOrder() + 1 < queueDao.count();
    }

    public boolean hasPrev(){
        return currentSong.getId() != 0;
    }

    public boolean hasPrevOnPlayOrder(){
        return currentSong.getPlayOrder() != 0;
    }


    public void goToNextSong(){
        if(isShuffling())
            currentSong = queueDao.findByPlayOrder(currentSong.getPlayOrder() + 1);
        else
            currentSong = queueDao.findById(currentSong.getSongId() + 1);
    }

    public void goToPrevSong(){
        if(isShuffling())
            currentSong = queueDao.findByPlayOrder(currentSong.getPlayOrder() - 1);
        else
            currentSong = queueDao.findById(currentSong.getSongId() - 1);
    }

    public boolean isShuffling(){
        return lastPlatedStateDao.getShufflingMode();
    }

    public void saveQueue(List<SongItem> queue){
        queueDb.runInTransaction(() -> {
            queueDao.deleteAll();
            queueDao.insertList(queue);

        });
    }

    public List<SongItem> getQueue() {
        return queueDao.getAll();
    }

    public void saveLastPlayedState(long timePlayed, boolean getShuffled, int getRepeat){
        lastPlayedStateItem = new LastPlayedStateItem(
                currentSong.getId(), currentSong.getSongId(),
                timePlayed, getShuffled, getRepeat);

        queueDb.runInTransaction(() -> {
            lastPlatedStateDao.delete();
            lastPlatedStateDao.insertAll(lastPlayedStateItem);
        });
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
