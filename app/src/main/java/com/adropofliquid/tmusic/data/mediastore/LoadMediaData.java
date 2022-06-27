package com.adropofliquid.tmusic.data.mediastore;

import android.app.Activity;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.views.MainActivity;
import com.adropofliquid.tmusic.views.adapters.AlbumViewListAdapter;
import com.adropofliquid.tmusic.views.adapters.ArtistViewListAdapter;
import com.adropofliquid.tmusic.views.mylibrary.album.AlbumsListAdapter;
import com.adropofliquid.tmusic.views.mylibrary.artist.ArtistsAdapter;
import com.adropofliquid.tmusic.views.mylibrary.SongListAdapter;

public class LoadMediaData extends AsyncTask<String, Void, String> {


    private Activity activity;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private int data;
    private long id;
    public static final int LOAD_ALL_SONG = 2;
    public static final int LOAD_SONGS_ALBUM = 5;
    public static final int LOAD_SONGS_ARTIST = 6;
    public static final int LOAD_ALL_ALBUM = 3;
    public static final int LOAD_ALL_ARTIST = 4;
    public static final int LOAD_ALBUMS = 7;


    public LoadMediaData(Activity activity, RecyclerView recyclerView, int data) {

        this.activity = activity;
        this.recyclerView = recyclerView;
        this.data = data;
    }

    public LoadMediaData(Activity activity,
                         RecyclerView recyclerView, int data, long id) {

        this.activity = activity;
        this.recyclerView = recyclerView;
        this.data = data;
        this.id = id;
    }

    @Override
    protected String doInBackground(String... strings) {

        switch (data){
            case LOAD_ALL_SONG:
                loadAllSongs();
                break;
            case LOAD_ALL_ALBUM:
                loadAllAlbums();
                break;
            case LOAD_ALL_ARTIST:
                loadAllArtists();
                break;
            case LOAD_SONGS_ALBUM:
                loadSongs(MainActivity.ALBUM_SONGS_LIST_VIEW);
                break;
            case LOAD_SONGS_ARTIST:
                loadSongs(MainActivity.ARTIST_LIST_VIEW);
                break;
            case LOAD_ALBUMS:
                loadAlbums();
                break;
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        recyclerView.setAdapter(adapter);
    }


    private void loadAllSongs(){
        adapter = new SongListAdapter(activity, new LoadMediaStore(activity).getAllSongs());
    }

    private void loadAllAlbums(){
        adapter = new AlbumsListAdapter(activity, new LoadMediaStore(activity).getAllAlbums());
    }

    private void loadAllArtists(){
        adapter = new ArtistsAdapter(activity, new LoadMediaStore(activity).getAllArtists());
    }

    private void loadSongs(int which){
        if(which == MainActivity.ALBUM_SONGS_LIST_VIEW)
            adapter = new AlbumViewListAdapter(activity, new LoadMediaStore(activity).getSongs(which,id));
        else
            adapter = new SongListAdapter(activity, new LoadMediaStore(activity).getSongs(which,id));

    }

    private void loadAlbums() {
        adapter = new ArtistViewListAdapter(activity,
                new LoadMediaStore(activity).getAlbums(id), id);
    }
}
