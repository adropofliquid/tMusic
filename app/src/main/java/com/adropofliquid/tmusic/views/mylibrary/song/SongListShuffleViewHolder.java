package com.adropofliquid.tmusic.views.mylibrary.song;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.data.SongRepository;

public class SongListShuffleViewHolder extends RecyclerView.ViewHolder{

    public SongListShuffleViewHolder(Activity activity, @NonNull View itemView) { //constructor
        super(itemView);
        itemView.setOnClickListener(v -> shuffleAllSongs(activity));
    }
    public void bindShuffleView(){
        //already hardcoded
    }
    private void shuffleAllSongs(Activity activity) {

        MediaControllerCompat.getMediaController(activity).getTransportControls()
                .setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);

        SongRepository songRepository = new SongRepository(activity);
        songRepository.shuffleSongs(song -> {
            Bundle bundle = new Bundle();
            bundle.putInt("song", song.getPlayOrder());

            MediaControllerCompat.getMediaController(activity).getTransportControls()
                    .skipToQueueItem(song.getPlayOrder());
        });
    }


}
