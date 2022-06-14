package com.adropofliquid.tmusic.views.adapters.holder;

import android.app.Activity;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.items.AlbumItem;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.playfromlist.Play;
import com.adropofliquid.tmusic.views.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class AllSongsViewHolder extends RecyclerView.ViewHolder{

    public AllSongsViewHolder(Activity activity, @NonNull View itemView, long artist) { //constructor
        super(itemView);

        itemView.setOnClickListener(v -> viewAlbumSongs(activity, artist));
    }
    public void bindShuffleView(){
        //already hardcoded
    }

    private void viewAlbumSongs(Activity activity,long artist) {
        ((MainActivity) activity).replaceFragment(MainActivity.SONG_LIST_VIEW, artist);
    }


}
