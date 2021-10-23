package com.adropofliquid.tmusic.views.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.items.AlbumHeaderView;
import com.adropofliquid.tmusic.items.AlbumItem;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.views.adapters.holder.AlbumHeaderViewHolder;
import com.adropofliquid.tmusic.views.adapters.holder.AllSongsViewHolder;
import com.adropofliquid.tmusic.views.adapters.holder.ShuffleViewHolder;
import com.adropofliquid.tmusic.views.adapters.holder.SongViewHolder;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class ArtistViewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
    private Activity activity;
    private static final String TAG = "SongList Adapter: ";
    private ArrayList<AlbumItem> viewAlbumList;
    private ArrayList<AlbumItem> albumList;

    public ArtistViewListAdapter(Activity activity, ArrayList<AlbumItem> albumList){

        this.activity = activity;
        this.albumList = albumList;
        this.viewAlbumList = new ArrayList<>();
        this.viewAlbumList.add(new AlbumItem());//shuffle is first on list
        this.viewAlbumList.add(new AlbumItem());//shuffle is first on list
        this.viewAlbumList.addAll(albumList);// merge song with shuffle
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:
                return new AlbumHeaderViewHolder(activity,
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.album_view_header,parent,false));
            case 1:
                return new AllSongsViewHolder(activity,LayoutInflater.from(parent.getContext()).inflate(R.layout.all_songs_card, parent, false), albumList.get(0).getArtist());
            default:
                return new SongViewHolder(activity,
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song,parent,false), albumList.get(viewType-2).getName());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case 0:
                ((AlbumHeaderViewHolder) holder).bindViews(
                        new AlbumHeaderView(
                                0,
                                albumList.get(position).getArtist(),
                                albumList.get(position).getArtist(),
                                albumList.size()), false);
                break;
            case 1:
                ((AllSongsViewHolder) holder).bindShuffleView();
                break;
            default:
                ((SongViewHolder) holder).bindAlbumViews(viewAlbumList.get(position));
        }
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return viewAlbumList.get(position).getName().substring(0,1);
    }

    @Override
    public int getItemCount() {
        return viewAlbumList.size();
    }

}
