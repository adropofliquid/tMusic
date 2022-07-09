package com.adropofliquid.tmusic.views.mylibrary.song;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import com.adropofliquid.tmusic.uncat.items.SongItem;
import com.adropofliquid.tmusic.views.adapters.NowPlayingAdapter;

import java.util.List;
import java.util.concurrent.Executor;

public class AdapterSpawn {

    private final Executor executor;
    public AdapterSpawn(Executor executor) {
        this.executor = executor;
    }

    public void spawn(Activity activity, List<SongItem> songs, OnAdapterReady onAdapterReady){
        executor.execute(()-> {
            RecyclerView.Adapter adapter = new SongListRecyclerAdapter(activity,songs);
            onAdapterReady.onReady(adapter);
        });
    }

    public void spawnNowPlayingAdapter(Activity activity,
                                       List<SongItem> songs,
                                       OnAdapterReady onAdapterReady){
        executor.execute(()-> {
            RecyclerView.Adapter adapter = new NowPlayingAdapter(activity,songs);
            onAdapterReady.onReady(adapter);
        });
    }

    public interface OnAdapterReady{
        void onReady(RecyclerView.Adapter adapter);
    }
}
