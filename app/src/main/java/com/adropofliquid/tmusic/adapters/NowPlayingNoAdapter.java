package com.adropofliquid.tmusic.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.service.Queue;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NowPlayingNoAdapter extends RecyclerView.Adapter<NowPlayingNoAdapter.MyViewHolder> {
    private final Activity activity;

    public NowPlayingNoAdapter(Activity activity) {

        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(activity).inflate(R.layout.card_now_playing, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(R.string.no_song);
        Glide.with(activity).load(R.drawable.musicplayer_library_default_album).centerCrop().into(holder.imageView); //set image

    }


    @Override
    public int getItemCount() {
        return 1;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.now_playing_title);
            imageView = itemView.findViewById(R.id.now_playing_album_art);
        }
    }
}
