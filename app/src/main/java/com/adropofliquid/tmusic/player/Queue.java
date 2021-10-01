package com.adropofliquid.tmusic.player;

import android.content.ContentQueryMap;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.db.room.QueueDao;
import com.adropofliquid.tmusic.db.room.QueueDb;
import com.adropofliquid.tmusic.items.SongItem;

import java.util.List;
import java.util.concurrent.Executor;

public class Queue {

    private final QueueDao queueDao;
    private final QueueDb queueDb;
    private final Executor executor;
    private List<SongItem> queue;
    private SongItem currentSong;
    private QueueChangeCallBack queueChangeCallBack;

    public Queue(Context context){
        this.queueDb = ((App)context.getApplicationContext()).getQueueDb(); //weird. Lol
        this.queueDao = queueDb.queueDao();

        this.executor = (((App) context.getApplicationContext()).getExecutor());

        queueChangeCallBack = null;
    }

    public SongItem getCurrentSong(int position){
        currentSong = queueDao.findByPosition(position);
        Log.i("Got it", currentSong.getTitle());
        return currentSong;
    }


    public void getCurrentSongA(int position, QueueChangeCallBack queueChangeCallBack){
        executor.execute(() -> {
            SongItem song = queueDao.findByPosition(position);
            //queueCallBack.onSongReady(song);
        });

    }

    public boolean hasNext(){
        return currentSong.getPosition() + 1 < queueDao.count();
    }

    public boolean hasPrev(){
        return currentSong.getPosition() !=0;
    }

    public void saveQueue(List<SongItem> queue){
        executor.execute(() -> {
            queueDb.runInTransaction(() -> {
                queueDao.deleteAll();
                queueDao.insertList(queue);
            });
        });
    }

    public void saveQueue(List<SongItem> queue, MediaControllerCompat.TransportControls playerController){
        //experiment to have an Arraylist Queue


        executor.execute(() -> {
            queueDb.runInTransaction(() -> {
                queueDao.deleteAll();
                queueDao.insertList(queue);
            });

            String action = "QueueChanged"; //FIXME change to onstant SUMTIN_QUEUE_CHANGED
            playerController.sendCustomAction(action, new Bundle());
        });


        //once insertion is complete, tell people
        this.queue = queue;


    }

    public void registerQueueChangeCallBackLListener(QueueChangeCallBack queueChangeCallBack){
        this.queueChangeCallBack = queueChangeCallBack;
    }

    interface QueueChangeCallBack {
        void onQueueSaved(List<SongItem> newQueue);
    }
}
