package com.adropofliquid.tmusic.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.adapters.holder.ShuffleViewHolder;
import com.adropofliquid.tmusic.adapters.holder.SongViewHolder;
import com.adropofliquid.tmusic.items.SongItem;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
    private static int SHUFFLE_ALL = 1;
    private static int SONGS_ITEM = 2;
    private Activity activity;
    private Context context;
    private static final String TAG = "SongList Adapter: ";
    ArrayList<SongItem> viewSongList;
    ArrayList<SongItem> songList;

    public SongListAdapter(Activity activity){
        this.context = activity.getApplicationContext();
        this.activity = activity;
        getSongList();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SHUFFLE_ALL) {
            return new ShuffleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song_shuffle,parent,false));
        } else {
            return new SongViewHolder(activity,LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song,parent,false),songList);
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

    private void getSongList() {

        viewSongList = new ArrayList<>();
        viewSongList.add(new SongItem(SongItem.TYPE_SHUFFLE));//shuffle is first on list
        getSongsDb();
        viewSongList.addAll(songList);// merge song with shuffle

//        getSongsDb(); //actual songs list
        //set SongLists from background thread
    }

    private void getSongsDb(){
        //TODO
        // get album name and save in SongItem

        songList = new ArrayList<>();
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

//        String selection = null;

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";



        ContentResolver contentResolver = context.getContentResolver(); // to get songs in storage
        Uri songLink = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //audio DB

        Cursor songCursor = contentResolver.query(songLink, projection, null, null, sortOrder);
        if (songCursor != null && songCursor.moveToFirst())
        {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumIdArt = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA); //handle file I/O

            do {
                String disTitle = songCursor.getString(songTitle);
                String disArtist = songCursor.getString(songArtist);
                String disAlbum = songCursor.getString(songAlbum);
                String disData = songCursor.getString(songData);
                long disAlbumId = songCursor.getLong(albumIdArt);
                int disDuration = songCursor.getInt(songDuration);

                songList.add(new SongItem(disTitle, disArtist,disAlbum, disAlbumId,
                        disDuration,disData, SongItem.TYPE_SONG));
            }
            while (songCursor.moveToNext());
        }
    }

}
