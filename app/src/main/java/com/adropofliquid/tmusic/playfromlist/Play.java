package com.adropofliquid.tmusic.playfromlist;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.media.session.MediaControllerCompat;

import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.queue.Queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Play {

    private final Activity activity;
    private final List<SongItem> songList;
    private final int songPosition;
    private final boolean shuffle;
    private final Queue queue;

    public Play(Activity activity, List<SongItem> songList, int songPosition, boolean shuffle) {
        this.activity = activity;
        this.songList = songList;
        this.songPosition = songPosition;
        this.shuffle = shuffle;
        this.queue =  new Queue(activity);
    }

    public void saveAndPlay() {
        new SaveQueueAndPlay().execute();
    }

    public void saveQueue(){
        queue.saveQueue(songList);
    }

    private void updateListPlayOrder() {

        List<Integer> shuffledPlayOrder = shuffledPlayOrder(songList.size());
        for(int i = 0; i < shuffledPlayOrder.size(); i++){
            songList.get(i).setPlayOrder(shuffledPlayOrder.get(i));
        }
    }

    public List<Integer> shuffledPlayOrder(int size){

        List<Integer> playOrder = new ArrayList<>();
        for(int i = 0; i < size; i++){
            playOrder.add(i);
        }
        Collections.shuffle(playOrder);

        return playOrder;
    }

    public void playFromPosition(){
        MediaControllerCompat.getMediaController(activity).getTransportControls()
                .skipToQueueItem(songPosition);
    }



    private class SaveQueueAndPlay extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            if(shuffle)
                updateListPlayOrder();

            saveQueue();

            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {
            playFromPosition();
        }

        @Override
        protected void onPreExecute() {
        }
    }
}