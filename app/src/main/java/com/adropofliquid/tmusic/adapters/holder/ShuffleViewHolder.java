package com.adropofliquid.tmusic.adapters.holder;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.db.QueueDbHelper;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.service.PlayerService;
import com.adropofliquid.tmusic.service.Queue;

import java.util.ArrayList;
import java.util.Collections;

public class ShuffleViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "Shuffle ViewHolder: ";

    public ShuffleViewHolder(Activity activity, @NonNull View itemView, ArrayList<SongItem> songList) { //constructor
        super(itemView);
        itemView.setOnClickListener(v -> shuffleAllSongs(activity,songList));
    }
    public void bindShuffleView(){
        //already hardcoded
    }
    private void shuffleAllSongs(Activity activity, ArrayList<SongItem> songList) {

        Queue.setOrderedQueue(songList);
        ArrayList<SongItem> songListShuffle = new ArrayList<>(songList);
        Collections.shuffle(songListShuffle);
        Queue.setQueue(songListShuffle);

        MediaControllerCompat.getMediaController(activity).getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
        MediaControllerCompat.getMediaController(activity).getTransportControls().skipToQueueItem(0);
/*
        ArrayList<SongItem> songListShuffle = new ArrayList<>(songList);

        //SEND QUEUE IN ORDER WITH INSTRUCTION TO SHUFFLE
        Collections.shuffle(songListShuffle);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(PlayerService.QUEUE_KEY,songListShuffle);

        MediaControllerCompat.getMediaController(activity).getTransportControls()
                .playFromMediaId(String.valueOf(getAdapterPosition()), bundle);
//        MediaControllerCompat.getMediaController(activity).getTransportControls().play();
        MediaControllerCompat.getMediaController(activity).getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);*/
        Log.d(TAG,"Shuffle All");
    }

}
