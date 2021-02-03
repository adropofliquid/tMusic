package com.adropofliquid.tmusic.adapters.holder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.service.PlayerService;

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
        Collections.shuffle(songList);
        Bundle bundle = new Bundle();
        //bundle.putInt(PlayerService.QUEUE_START_KEY, adapterPosition);
        bundle.putParcelableArrayList(PlayerService.QUEUE_KEY,songList);

        MediaControllerCompat.getMediaController(activity).getTransportControls()
                .playFromMediaId(String.valueOf(0), bundle);
        Log.d(TAG,"Shuffle All");
    }

}
