package com.adropofliquid.tmusic.views.adapters.holder;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.uncat.items.AlbumHeaderView;
import com.bumptech.glide.Glide;

public class AlbumHeaderViewHolder extends RecyclerView.ViewHolder{

    private final ImageView albumArt;
    private final TextView artist;
    private final TextView songCount;
    private final TextView albumName;
    private final Activity activity;

    public AlbumHeaderViewHolder(Activity activity,@NonNull View itemView) {
        super(itemView);

        this.activity = activity;
        albumArt = itemView.findViewById(R.id.album_view_art);
        artist = itemView.findViewById(R.id.album_view_artist);
        songCount = itemView.findViewById(R.id.album_view_song_count);
        albumName = itemView.findViewById(R.id.album_view_song_title);
    }

    public void bindSongsViews(AlbumHeaderView albumHeaderView){

        Glide.with(activity).load(albumHeaderView.getAlbumArtUri())
                .error(R.drawable.miniplayer_default_album_art)
                .centerCrop()
                .into(albumArt);

        artist.setText(albumHeaderView.getArtist());
        albumName.setText(albumHeaderView.getAlbum());

        if(albumHeaderView.getCount() == 1)
            songCount.setText(albumHeaderView.getCount() + " song");
        else
            songCount.setText(albumHeaderView.getCount() + " songs");

    }

    public void bindViews(AlbumHeaderView albumHeaderView, boolean art){

        albumName.setText(albumHeaderView.getArtist());

        if(albumHeaderView.getCount() == 1)
            artist.setText(albumHeaderView.getCount() + " album");
        else
            artist.setText(albumHeaderView.getCount() + " albums");

        songCount.setText("10 Tracks");
    }

}
