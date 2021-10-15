package com.adropofliquid.tmusic.views.adapters.holder;

import android.app.Activity;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.playfromlist.Play;

import java.util.ArrayList;

public class ShuffleViewHolder extends RecyclerView.ViewHolder{

    public ShuffleViewHolder(Activity activity, @NonNull View itemView, ArrayList<SongItem> songList) { //constructor
        super(itemView);
        itemView.setOnClickListener(v -> shuffleAllSongs(activity,songList));
    }
    public void bindShuffleView(){
        //already hardcoded
    }
    private void shuffleAllSongs(Activity activity, ArrayList<SongItem> songList) {

        MediaControllerCompat.getMediaController(activity).getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);

        Play play = new Play(activity,
                songList,
                0,
                true);

        play.saveAndPlay();

    }


}
