package com.adropofliquid.tmusic.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.adapters.holder.AlbumViewHolder;
import com.adropofliquid.tmusic.adapters.holder.ShuffleViewHolder;
import com.adropofliquid.tmusic.adapters.holder.SongViewHolder;
import com.adropofliquid.tmusic.items.AlbumItem;
import com.adropofliquid.tmusic.items.SongItem;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
    
    private Activity activity;
    private static final String TAG = "SongList Adapter: ";
    private ArrayList<AlbumItem> albumList;

    public AlbumsAdapter(Activity activity, ArrayList<AlbumItem> songList){
        this.activity = activity;
        this.albumList = songList;
    }


    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumViewHolder(activity,
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song,parent,false),
                    albumList);
        
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder,final int position) {
        holder.bindSongsViews(albumList.get(position));
    }


    @NonNull
    @Override
    public String getSectionName(int position) {
        return albumList.get(position).getName().substring(0,1);
    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
