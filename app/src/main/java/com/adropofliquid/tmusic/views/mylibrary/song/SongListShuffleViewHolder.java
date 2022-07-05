package com.adropofliquid.tmusic.views.mylibrary.song;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.data.SongRepository;
import com.adropofliquid.tmusic.uncat.items.SongItem;

import java.util.List;

public class SongListShuffleViewHolder extends RecyclerView.ViewHolder{

    public SongListShuffleViewHolder(Activity activity, @NonNull View itemView) { //constructor
        super(itemView);
        itemView.setOnClickListener(v -> shuffleAllSongs(activity));
    }
    public void bindShuffleView(){
        //already hardcoded
    }
    private void shuffleAllSongs(Activity activity) {


        SongRepository songRepository = new SongRepository(activity);
        songRepository.loadShuffledSongs(activity, song -> {
            Bundle bundle = new Bundle();
            bundle.putInt("song", song.getPlayOrder());

            MediaControllerCompat.getMediaController(activity).getTransportControls()
                    .playFromMediaId("song", bundle);
        });

        MediaControllerCompat.getMediaController(activity).getTransportControls()
                .setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
    }


}
