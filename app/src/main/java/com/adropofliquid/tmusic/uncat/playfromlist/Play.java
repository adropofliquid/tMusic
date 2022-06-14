package com.adropofliquid.tmusic.uncat.playfromlist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.uncat.items.SongItem;
import com.adropofliquid.tmusic.data.queue.Queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public class Play {

    private final Activity activity;
    private final List<SongItem> songList;
    private int songPosition;
    private final boolean shuffle;
    private final Queue queue;
    private OnShuffledCallback onShuffledCallback;

    public Play(Activity activity, List<SongItem> songList, int songPosition, boolean shuffle) {
        this.activity = activity;
        this.songList = songList;
        this.songPosition = songPosition;
        this.shuffle = shuffle;
        this.queue =  new Queue(activity);
    }

    public void saveQueue(){
        Executor executor = (((App) activity.getApplicationContext()).getExecutor());
        executor.execute(() -> {
            if(shuffle)
                updateListPlayOrder();
            new Queue(activity).saveQueue(songList);
        });
    }

    public void playSelected(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", songList.get(songPosition));

        MediaControllerCompat.getMediaController(activity).getTransportControls()
                .playFromMediaId("song", bundle);
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
        onShuffledCallback.onShuffled(playOrder.get(0));
        return playOrder;
    }


    public void setSongPosition(int position){
        songPosition = position;
    }

    public void registerOnShuffledCallback(OnShuffledCallback onShuffledCallback){
        this.onShuffledCallback = onShuffledCallback;
    }

    public interface OnShuffledCallback {
        void onShuffled(int firstOnList);
    }

}