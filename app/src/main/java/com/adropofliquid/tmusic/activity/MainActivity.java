package com.adropofliquid.tmusic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.adapters.SongListAdapter;
import com.adropofliquid.tmusic.db.LoadMediaStore;
import com.adropofliquid.tmusic.dialog.NeedPermission;
import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.service.PlayerService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_STORAGE = 1;
    private static final String TAG = "MainActivity: ";

    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController = null;

    private ImageView bottomPlayerImage;
    private TextView bottomPlayerTitle;
    private TextView bottomPlayerArtist;
    private ProgressBar progressBar;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_library);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, PlayerService.class),
                connectionCallbacks,
                null);

        checkStoragePermission();

        bottomPlayerImage = findViewById(R.id.bottom_player_image);
        bottomPlayerTitle = findViewById(R.id.bottom_player_name);
        bottomPlayerArtist = findViewById(R.id.bottom_player_artist);
        progressBar = findViewById(R.id.progressBar);

        ConstraintLayout bottomPlayer = findViewById(R.id.bottom_player);
        bottomPlayer.setOnClickListener(new PlayerOnclickListener());


        //TODO
        //do sumtin about the buttons wen no media to play
        //replace dem wit some faint shii or sumtin
        //only register onclick listener wen dere is sumtin playing

        ImageButton bottomPlayerPrev = findViewById(R.id.bottom_player_prev);
        bottomPlayerPrev.setOnClickListener(new PlayerOnclickListener());
        ImageButton bottomPlayerPlay = findViewById(R.id.bottom_player_play);
        bottomPlayerPlay.setOnClickListener(new PlayerOnclickListener());
        ImageButton bottomPlayerNext = findViewById(R.id.bottom_player_next);
        bottomPlayerNext.setOnClickListener(new PlayerOnclickListener());
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onStop() {
        super.onStop();
        // (see "stay in sync with the MediaSession")
        if (mediaController != null) {
            mediaController.unregisterCallback(controllerCallback);
        }
        mediaBrowser.disconnect();
    }

    private void populateRecycler() {
        Log.d(TAG, "Recycler View is being populated");

        RecyclerView.LayoutManager layoutManager;
        recyclerView = findViewById(R.id.song_list_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        new LoadGenre().execute("");
    }

    private class LoadGenre extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            adapter = new SongListAdapter(MainActivity.this,
                    new LoadMediaStore(getApplicationContext()).getAllSongs());
            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(adapter);
        }
        @Override
        protected void onPreExecute() {
        }
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    // Get the token for the MediaSession
                    MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

                    mediaController = new MediaControllerCompat(MainActivity.this, // Context
                            token);

                    // Save the controller
                    MediaControllerCompat.setMediaController(MainActivity.this, mediaController);

                    // Finish building the UI
                    buildTransportControls();
                    Log.v(TAG,"Control Connected");
//                    Toast.makeText(MainActivity.this, "Control Connected", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onConnectionSuspended() {
                    // The Service has crashed. Disable transport controls until it automatically reconnects
                    Log.v(TAG,"Suspended");
                }

                @Override
                public void onConnectionFailed() {
                    // The Service has refused our connection
                    Log.v(TAG,"Failed");
                }
            };

    private final MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    //do some UI shii to change view if metadata is not empty
                    Log.d(TAG,"Meta Changed to: "+metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));

                    //update mini player
                    bottomPlayerImage.setImageBitmap(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ART));
                    bottomPlayerTitle.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                    bottomPlayerArtist.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
                    progressBar.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    progressBar.setProgress((int) state.getPosition());
                }
            };

    private void buildTransportControls() {
        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);

        if(mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING){
            //FIXME it doesn't update when paused.
            //do better

            MediaMetadataCompat metadata = mediaController.getMetadata();

            bottomPlayerImage.setImageBitmap(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ART));
            bottomPlayerTitle.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            bottomPlayerArtist.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

            progressBar.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        }
    }

    private void checkStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Permission granted");
            populateRecycler();
        }
        else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                NeedPermission needPermission = new NeedPermission();
                needPermission.show(getSupportFragmentManager(),"need_permission");
            }
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission granted");
            populateRecycler();
        } else {
            //FIXME pops at the wrong time
            Log.d(TAG, "Permission not granted");
            NeedPermission needPermission = new NeedPermission();
            needPermission.show(getSupportFragmentManager(),"need_permission");
        }
    }

    //TODO can create a whole class to
    // manage wen player plays or stop or restarts
    // Class would connect to the controller so it can be getting playback state
    private class PlayerOnclickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.bottom_player:
                    Intent intent = new Intent(getApplicationContext(),NowPlaying.class);
                    startActivity(intent);
                    break;
                case R.id.bottom_player_prev:
                    mediaController.getTransportControls().skipToPrevious();
                    break;
                case R.id.bottom_player_play:
                    if(!(mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING))
                        mediaController.getTransportControls().play();
                    else
                        mediaController.getTransportControls().pause();
                    break;
                case R.id.bottom_player_next:
                    mediaController.getTransportControls().skipToNext();
                    break;
            }
        }
    }
}