package com.adropofliquid.tmusic.adapters.holder;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShuffleViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "Shuffle ViewHolder: ";

    public ShuffleViewHolder(@NonNull View itemView) { //constructor
        super(itemView);
        itemView.setOnClickListener(v -> shuffleAllSongs());
    }
    public void bindShuffleView(){
        //already hardcoded
    }
    private void shuffleAllSongs() {
        Log.d(TAG,"Shuffle All");
    }

}
