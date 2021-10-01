package com.adropofliquid.tmusic.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.adapters.SongListAdapter;
import com.adropofliquid.tmusic.db.LoadMediaStore;
import com.adropofliquid.tmusic.dialog.NeedPermission;
import com.adropofliquid.tmusic.fragment.MyLibraryFragment;
import com.adropofliquid.tmusic.player.OldPlayList;
import com.adropofliquid.tmusic.player.PlayerService;
import com.bumptech.glide.Glide;

public class MainActivityFrag extends AppCompatActivity {

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
    private ImageButton bottomPlayerPrev;
    private ImageButton bottomPlayerPlay;
    private ImageButton bottomPlayerNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, MyLibraryFragment.class, null)
                    .commit();
        }

        checkStoragePermission();

        initialize();
    }

    private void initialize(){

        //        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_library);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, PlayerService.class),
                connectionCallbacks,
                null);

        bottomPlayerImage = findViewById(R.id.bottom_player_image);
        bottomPlayerTitle = findViewById(R.id.bottom_player_name);
        bottomPlayerArtist = findViewById(R.id.bottom_player_artist);



        progressBar = findViewById(R.id.progressBar);

        ConstraintLayout bottomPlayer = findViewById(R.id.bottom_player);
        bottomPlayer.setOnClickListener(new PlayerOnclickListener());

        bottomPlayerPrev = findViewById(R.id.bottom_player_prev);
        bottomPlayerPlay = findViewById(R.id.bottom_player_play);
        bottomPlayerNext = findViewById(R.id.bottom_player_next);

        bottomPlayerPlay.setOnClickListener(new PlayerOnclickListener());
        bottomPlayerPrev.setOnClickListener(new PlayerOnclickListener());
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkStoragePermission() {
        //TODO Put permission check/request in DB class
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Permission granted");
            //populateRecycler();
            //do nuthin
        }
        else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                NeedPermission needPermission = new NeedPermission();
                needPermission.show(getSupportFragmentManager(),"need_permission");
            }
            ActivityCompat.requestPermissions(MainActivityFrag.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission granted");
            //populateRecycler();
        } else {
            //FIXME pops at the wrong time
            Log.d(TAG, "Permission not granted");
            NeedPermission needPermission = new NeedPermission();
            needPermission.show(getSupportFragmentManager(),"need_permission");
        }
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

    private void buildTransportControls() {

        if(!OldPlayList.isEmpty()){
            Glide.with(this)
                    .load(OldPlayList.getCurrentSong().getAlbumArtUri())
                    .centerCrop().into(bottomPlayerImage); //set image

            bottomPlayerTitle.setText(OldPlayList.getCurrentSong().getTitle());
            bottomPlayerArtist.setText(OldPlayList.getCurrentSong().getArtist());

            changePausePlayButtons();

            progressBar.setProgress(OldPlayList.getSongProgress());
            progressBar.setMax(OldPlayList.getCurrentSong().getDuration());
        }

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);
    }

    private class LoadGenre extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            adapter = new SongListAdapter(MainActivityFrag.this,
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

                    mediaController = new MediaControllerCompat(MainActivityFrag.this, // Context
                            token);

                    // Save the controller
                    MediaControllerCompat.setMediaController(MainActivityFrag.this, mediaController);

                    // Finish building the UI
                    buildTransportControls();
                    Log.v(TAG,"Control Connected");
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

                    //update mini player
                    bottomPlayerImage.setImageBitmap(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ART));
                    bottomPlayerTitle.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                    bottomPlayerArtist.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
                    progressBar.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    changePausePlayButtons();
                    progressBar.setProgress((int) state.getPosition());
                }
            };

    private void changePausePlayButtons(){
        if((mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING))
            bottomPlayerPlay.setImageResource(R.drawable.ic_baseline_pause_24);
        else
            bottomPlayerPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
    }

    private class PlayerOnclickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(!OldPlayList.isEmpty())
            {
                switch (view.getId()) {
                    case R.id.bottom_player:
                        Intent intent = new Intent(getApplicationContext(), NowPlaying.class);
                        startActivity(intent);
                        break;
                    case R.id.bottom_player_prev:
                        mediaController.getTransportControls().skipToPrevious();
                        break;
                    case R.id.bottom_player_play:
                        if (mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                            mediaController.getTransportControls().pause();

                        } else {
                            mediaController.getTransportControls().play();
                        }
                        break;
                    case R.id.bottom_player_next:
                        mediaController.getTransportControls().skipToNext();
                        break;
                }
            }
        }
    }
}