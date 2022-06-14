package com.adropofliquid.tmusic.views.fragment.mylibrary.artist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.items.ArtistItem;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistViewHolder> implements FastScrollRecyclerView.SectionedAdapter{

    private Activity activity;
    private static final String TAG = "SongList Adapter: ";
    private ArrayList<ArtistItem> artistList;

    public ArtistsAdapter(Activity activity, ArrayList<ArtistItem> songList){
        this.activity = activity;
        this.artistList = songList;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistViewHolder(activity,
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song,parent,false),
                artistList);
        
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder,final int position) {
        holder.bindSongsViews(artistList.get(position));
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return artistList.get(position).getArtist().substring(0,1);
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

}
