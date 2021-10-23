package com.adropofliquid.tmusic.views.adapters.holder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.items.AlbumItem;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.playfromlist.Play;
import com.adropofliquid.tmusic.queue.Queue;
import com.adropofliquid.tmusic.views.activity.MainActivity;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
    private final ImageView songArt;
    private final TextView songName;
    private final TextView songArtist;
    private final ImageButton songOptions;
    private final Activity activity;


    public SongViewHolder(Activity activity,
                          @NonNull View itemView,
                          List<SongItem> songList, int offset)  { //constructor
        super(itemView);

        this.activity = activity;

        songArt = itemView.findViewById(R.id.song_art);
        songName = itemView.findViewById(R.id.song_name);
        songArtist = itemView.findViewById(R.id.song_artist);
        songOptions = itemView.findViewById(R.id.song_options);

        itemView.setOnClickListener(v -> playSongList(songList,getAdapterPosition() - offset));
        itemView.setOnLongClickListener(v -> {
            showPopup(v);
            return true;
        });

        songOptions.setOnCreateContextMenuListener(this);
        songOptions.setOnClickListener(v -> songOptions.showContextMenu());
    }

    public SongViewHolder(Activity activity,
                          @NonNull View itemView, String album)  { //constructor
        super(itemView);

        this.activity = activity;

        songArt = itemView.findViewById(R.id.song_art);
        songName = itemView.findViewById(R.id.song_name);
        songArtist = itemView.findViewById(R.id.song_artist);
        songOptions = itemView.findViewById(R.id.song_options);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).replaceFragment(MainActivity.ALBUM_LIST_VIEW, album);
            }
        });

        //itemView.setOnClickListener(v -> playSongList(songList,getAdapterPosition() - offset));
        itemView.setOnLongClickListener(v -> {
            showPopup(v);
            return true;
        });

        songOptions.setOnCreateContextMenuListener(this);
        songOptions.setOnClickListener(v -> songOptions.showContextMenu());
    }

    public void bindSongsViews(SongItem songItem){
        Glide.with(activity).load(songItem.getAlbumArtUri())
                .error(R.drawable.miniplayer_default_album_art)
                .centerCrop()
                .into(songArt); //set image

        songName.setText(songItem.getTitle());
        songArtist.setText(songItem.getArtist());
    }

    public void bindAlbumViews(AlbumItem albumItem){
        Glide.with(activity).load(albumItem.getAlbumArtUri())
                .error(R.drawable.miniplayer_default_album_art)
                .centerCrop()
                .into(songArt); //set image

        songName.setText(albumItem.getName());
//        songArtist.setText(albumItem.getYear());
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.song_items, contextMenu);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(activity, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.song_items, popup.getMenu());
        popup.show();
    }

    private void playSongList(List<SongItem> songList, int adapterPosition) {

        shuffleAndRepeat();

        Play play = new Play(activity, songList, adapterPosition, false);

        play.saveQueue();
        play.playSelected();
//        Log.v("Song Album:", songList.get(adapterPosition).getAlbumId()+"");
    }

    private void playSelected(SongItem song){
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", song);

        Log.v("The Song",song.getTitle());
        MediaControllerCompat.getMediaController(activity).getTransportControls()
                .playFromMediaId("song", bundle);
    }

    private void saveQueue(List<SongItem> queue){
        Executor executor = (((App) activity.getApplicationContext()).getExecutor());
        executor.execute(() -> {
            new Queue(activity).saveQueue(queue);
        });
    }

    private void shuffleAndRepeat(){
        MediaControllerCompat.getMediaController(activity).getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
        MediaControllerCompat.getMediaController(activity).getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
    }
}
