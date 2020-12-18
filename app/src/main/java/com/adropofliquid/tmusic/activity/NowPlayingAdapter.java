package com.adropofliquid.tmusic.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.service.PlayerService;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.MyViewHolder> {
    private final Activity activity;
    private final ArrayList<SongItem> songList;

    public NowPlayingAdapter(Activity activity) {

        this.activity = activity;
        Bundle bundle  = MediaControllerCompat.getMediaController(activity).getExtras();
        bundle.setClassLoader(SongItem.class.getClassLoader());
        this.songList = bundle.getParcelableArrayList(PlayerService.QUEUE_KEY);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.card_now_playing, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textView.setText(songList.get(position).getTitle());
        holder.textView2.setText(songList.get(position).getArtist());
        holder.textView3.setText(songList.get(position).getAlbum());
        Glide.with(activity.getApplicationContext()).load(songList.get(position).getAlbumArtUri()).centerCrop().into(holder.imageView); //set image
        /*try {
            holder.imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(context.getContentResolver(),Queue.getSong(position).getAlbumArtUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        TextView textView2;
        TextView textView3;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.now_playing_title);
            textView2 = itemView.findViewById(R.id.now_playing_artist);
            textView3 = itemView.findViewById(R.id.now_playing_album);
            imageView = itemView.findViewById(R.id.now_playing_album_art);
        }
    }
}
