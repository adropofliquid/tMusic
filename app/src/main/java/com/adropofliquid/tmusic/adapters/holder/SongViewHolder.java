package com.adropofliquid.tmusic.adapters.holder;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaControllerCompat;
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
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.service.PlayerService;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
    private static final String TAG = "Song Clicked: ";
    private final ImageView songArt;
    private final TextView songName;
    private final TextView songArtist;
    private final ImageButton songOptions;
    private final Activity activity;


    public SongViewHolder(Activity activity, @NonNull View itemView, ArrayList<SongItem> songList) { //constructor
        super(itemView);

        this.activity = activity;

        songArt = itemView.findViewById(R.id.song_art);
        songName = itemView.findViewById(R.id.song_name);
        songArtist = itemView.findViewById(R.id.song_artist);
        songOptions = itemView.findViewById(R.id.song_options);

        itemView.setOnClickListener(v -> playSongList(songList,getAdapterPosition() -1));
        itemView.setOnLongClickListener(v -> {
            showPopup(v);
            return true;
        });

        songOptions.setOnCreateContextMenuListener(this);
        songOptions.setOnClickListener(v -> songOptions.showContextMenu());
    }

    private void playSongList(ArrayList<SongItem> songList, int adapterPosition) {

        Bundle bundle = new Bundle();
        //bundle.putInt(PlayerService.QUEUE_START_KEY, adapterPosition);
        bundle.putParcelableArrayList(PlayerService.QUEUE_KEY,songList);

        MediaControllerCompat.getMediaController(activity).getTransportControls()
                .playFromMediaId(String.valueOf(adapterPosition), bundle);
//        Log.d(TAG," "+adapterPosition+". "+songList.get(adapterPosition).getTitle()+ " "+songList.size());

    }

    public void bindSongsViews(SongItem songItem){
        //TODO set for when image doesn't exist
        Glide.with(activity.getApplicationContext()).load(songItem.getAlbumArtUri()).centerCrop().into(songArt); //set image
        /*try {
            songArt.setImageBitmap(MediaStore.Images.Media.getBitmap(activity.getContentResolver(),songItem.getAlbumArtUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        songName.setText(songItem.getTitle());
        songArtist.setText(songItem.getArtist());
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
}
