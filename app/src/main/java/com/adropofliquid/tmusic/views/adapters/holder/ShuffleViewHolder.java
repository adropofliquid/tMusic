package com.adropofliquid.tmusic.views.adapters.holder;

import android.app.Activity;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.uncat.items.SongItem;

import java.util.List;

public class ShuffleViewHolder extends RecyclerView.ViewHolder{

    public ShuffleViewHolder(Activity activity, @NonNull View itemView, List<SongItem> songList) { //constructor
        super(itemView);
        itemView.setOnClickListener(v -> shuffleAllSongs(activity,songList));
    }
    public void bindShuffleView(){
        //already hardcoded
    }
    private void shuffleAllSongs(Activity activity, List<SongItem> songList) {

//        MediaControllerCompat.getMediaController(activity).getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
//
//        Play play = new Play(activity,
//                songList,
//                0,
//                true);
//
//        play.saveQueue();
//
//        play.registerOnShuffledCallback(firstOnList -> {
//            play.setSongPosition(firstOnList);
//            play.playSelected();
//        });

    }


}
