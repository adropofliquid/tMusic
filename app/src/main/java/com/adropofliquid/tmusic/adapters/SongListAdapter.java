package com.adropofliquid.tmusic.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.items.SongItem;
import com.bumptech.glide.Glide;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class SongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
    private static int SHUFFLE_ALL = 1;
    private static int SONGS_ITEM = 2;
    private Context context;
    private static final String TAG = "SongList Adapter: ";
    ArrayList<SongItem> songList;

    public SongListAdapter(Context context){
        this.context = context;
        getSongList();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SHUFFLE_ALL) {
            return new ShuffleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song_shuffle,parent,false));
        } else {
            return new SongViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song,parent,false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (songList.get(position).getSongType() == SongItem.TYPE_SHUFFLE) {
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
            ((SongViewHolder) holder).bindSongsViews(songList.get(position));
        }
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return songList.get(position).getTitle().substring(0,1);
        //TODO fix for shuffle ish
    }

    private class SongViewHolder extends RecyclerView.ViewHolder{
        public ImageView songArt;
        public TextView songName;
        public TextView songArtist;
        public TextView songOptions;

        public void bindSongsViews(SongItem songItem)
        {
            Glide.with(context).load(songItem.getAlbumArt()).centerCrop().into(songArt); //set image
            songName.setText(songItem.getTitle());
            songArtist.setText(songItem.getArtist());
            songOptions.setText(R.string.three_dots);
        }


        public SongViewHolder(@NonNull View itemView) { //constructor
            super(itemView);
            songArt = itemView.findViewById(R.id.song_art);
            songName = itemView.findViewById(R.id.song_name);
            songArtist = itemView.findViewById(R.id.song_artist);
            songOptions = itemView.findViewById(R.id.song_options);

            itemView.setOnClickListener(v -> {
                //Toast.makeText(context, textView.getText(), Toast.LENGTH_SHORT).show();
                //startSong(getAdapterPosition() - 1);
                Log.d(TAG,"Song Clicked");
            });

            itemView.setOnLongClickListener(v -> {
                //showPopup(v);
                return true;
            });
        }

    }

    private class ShuffleViewHolder extends RecyclerView.ViewHolder{
//        public ImageView imageView;
//        public TextView textView;

        public void bindShuffleView(){
            //already hardcoded
        }

        public ShuffleViewHolder(@NonNull View itemView) { //constructor
            super(itemView);
            //we don't need all 'at
//            imageView = itemView.findViewById(R.id.imageView);
//            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(v -> shuffleAllSongs());
        }

    }

    private void shuffleAllSongs() {
        Log.d(TAG,"Shuffle All");
    }


    @Override
    public int getItemCount() {
        return songList.size();
    }

    private void getSongList() {
        songList = new ArrayList<>();
        songList.add(new SongItem(SongItem.TYPE_SHUFFLE));//shuffle is first on list

        getSongsDb(); //continue adding actual songs
        //set SongLists from background thread
    }

    private void getSongsDb(){
        //TODO
        // get album name and save in SongItem

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
