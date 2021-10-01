package com.adropofliquid.tmusic.player;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import com.adropofliquid.tmusic.db.room.QueueDb;
import com.adropofliquid.tmusic.items.SongItem;
import java.util.ArrayList;

public class OldPlayList {

    private static ArrayList<SongItem> queue = null;
    private static ArrayList<SongItem> orderedQueue = null;
    private static int currentSongPos = 0;
    private static int currentPos = 0;
    private final String TAG = "Queue: ";

    public static void setQueue(ArrayList<SongItem> queue) {
        OldPlayList.queue = queue;

    }


    public static void updateQueue(ArrayList<SongItem> updatedQueue){
        queue.clear();
        queue.addAll(updatedQueue);
    }

    public static ArrayList<SongItem> getQueue() {
        return queue;
    }

    public static int getCurrentSongPos() {
        return currentSongPos;
    }

    public static void setCurrentSongPos(int position) {
        OldPlayList.currentSongPos = position;
    }

    public static SongItem getCurrentSong() {
        return queue.get(currentSongPos);
//        QueueDao queueDao = MainActivity.db.queueDao();
//        return makeItSong(queueDao.findByPosition(currentSongPos));
    }

    public static boolean hasNext(){
        return getCurrentSongPos() + 1 < queue.size();
    }

    public static boolean hasPrev(){
        return getCurrentSongPos() != 0;
    }

    public static ArrayList<SongItem> getOrderedQueue() {
        return orderedQueue;
    }

    public static void setOrderedQueue(ArrayList<SongItem> orderedQueue) {
        OldPlayList.orderedQueue = orderedQueue;
    }

    public static boolean isEmpty() {
        return queue == null || queue.isEmpty();
    }

    public static void setSongProgress(int currentPosition) {
        currentPos = currentPosition;
    }

    public static int getSongProgress() {
        return currentPos;
    }

    public static void saveDatabase(Context context){
        new LoadGenre(context).execute();
//        queue.forEach((q)-> db.queueDao().insertAll(q));
    }

    public static void loadFromDb(Context context){
        // FIXME shuffle was confused

        QueueDb db;
        db = Room.databaseBuilder(context,
                QueueDb.class, "queue").allowMainThreadQueries().build();

        queue = (ArrayList<SongItem>) db.queueDao().getAll();
    }


    private static class LoadGenre extends AsyncTask<String, Void, String> {

        private final Context context;

        public LoadGenre(Context context){
            this.context = context;
        }
        @Override
        protected String doInBackground(String... strings) {
            QueueDb db;
            db = Room.databaseBuilder(context,
                    QueueDb.class, "queue").allowMainThreadQueries().build();

            db.queueDao().deleteAll();
            db.queueDao().insertList(queue);
            Log.d("Awon ti Queue"," We are Safe");
            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {
            //recyclerView.setAdapter(adapter);
        }
        @Override
        protected void onPreExecute() {
        }
    }

}