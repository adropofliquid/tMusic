package com.adropofliquid.tmusic.views.adapters.holder;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.views.MainActivity;

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
