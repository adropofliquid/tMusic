package com.adropofliquid.tmusic.data.queue;

import android.content.Context;

import com.adropofliquid.tmusic.uncat.items.SongItem;
import com.adropofliquid.tmusic.uncat.mediastore.LoadMediaStore;

import java.util.List;
import java.util.concurrent.Executor;

public class SongRepository {


    private final Executor executor;

    public SongRepository(Executor executor){
        this.executor = executor;
    }

    public void loadSongs(Context context, OnSongsLoadedCallback onSongsLoadedCallback) {
        executor.execute(()->{
            List<SongItem> songs = new LoadMediaStore(context).getAllSongs();
            onSongsLoadedCallback.onLoaded(songs);
        });
    }

    public interface OnSongsLoadedCallback{
        void onLoaded(List<SongItem> songs);
    }
}
