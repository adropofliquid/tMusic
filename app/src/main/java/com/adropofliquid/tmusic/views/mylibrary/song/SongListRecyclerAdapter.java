package com.adropofliquid.tmusic.views.mylibrary.song;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.uncat.items.SongItem;
import com.adropofliquid.tmusic.views.adapters.holder.ShuffleViewHolder;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class SongListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
    private Activity activity;
    private static final String TAG = "SongList Adapter: ";
    private ArrayList<SongItem> viewSongList;
    private ArrayList<SongItem> songList;

    public SongListRecyclerAdapter(Activity activity, ArrayList<SongItem> songList){

        this.activity = activity;
        this.songList = songList;
        this.viewSongList = new ArrayList<>();
        this.viewSongList.add(new SongItem(SongItem.TYPE_SHUFFLE));//shuffle is first on list
        this.viewSongList.addAll(songList);// merge song with shuffle
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SongItem.TYPE_SHUFFLE) {
            return new SongListShuffleViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.card_song_shuffle, parent, false), songList);
        }
        return new SongViewHolder(activity,
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_song, parent, false), songList,1);
    }

    @Override
    public int getItemViewType(int position) {
        return viewSongList.get(position).getSongType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == SongItem.TYPE_SHUFFLE) {
            ((SongListShuffleViewHolder) holder).bindShuffleView();
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
