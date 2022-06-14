package com.adropofliquid.tmusic.views.mylibrary.song;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.adropofliquid.tmusic.uncat.items.SongItem;
import java.util.List;

public class SongListShuffleViewHolder extends RecyclerView.ViewHolder{

    public SongListShuffleViewHolder(@NonNull View itemView, List<SongItem> songList) { //constructor
        super(itemView);
        itemView.setOnClickListener(v -> shuffleAllSongs(songList));
    }
    public void bindShuffleView(){
        //already hardcoded
    }
    private void shuffleAllSongs(List<SongItem> songList) {

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
