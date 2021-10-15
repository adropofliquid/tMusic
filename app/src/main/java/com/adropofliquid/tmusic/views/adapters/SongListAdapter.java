package com.adropofliquid.tmusic.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.views.adapters.holder.ShuffleViewHolder;
import com.adropofliquid.tmusic.views.adapters.holder.SongViewHolder;
import com.adropofliquid.tmusic.items.SongItem;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
    private static int SHUFFLE_ALL = 1;
    private static int SONGS_ITEM = 2;
    private Activity activity;
    private static final String TAG = "SongList Adapter: ";
    private ArrayList<SongItem> viewSongList;
    private ArrayList<SongItem> songList;

    public SongListAdapter(Activity activity, ArrayList<SongItem> songList){
        this.activity = activity;

        this.songList = songList;
        this.viewSongList = new ArrayList<>();
        this.viewSongList.add(new SongItem(SongItem.TYPE_SHUFFLE));//shuffle is first on list
        this.viewSongList.addAll(songList);// merge song with shuffle
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SHUFFLE_ALL) {
            return new ShuffleViewHolder(activity,
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song_shuffle, parent, false),
                    songList);
        } else {
            return new SongViewHolder(activity,
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song,parent,false),
                    songList);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (viewSongList.get(position).getSongType() == SongItem.TYPE_SHUFFLE) {
            return SHUFFLE_ALL;
        } else {
            return SONGS_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == SHUFFLE_ALL) {
            ((ShuffleViewHolder) holder).bindShuffleView();
        } else {
            ((SongViewHolder) holder).bindSongsViews(viewSongList.get(position));
        }
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return viewSongList.get(position).getTitle().substring(0,1);
    }


    @Override
    public int getItemCount() {
        return viewSongList.size();
    }

}
