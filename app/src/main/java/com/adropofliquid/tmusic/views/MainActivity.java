package com.adropofliquid.tmusic.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.data.SongRepository;
import com.adropofliquid.tmusic.uncat.permissions.SystemPermissions;
import com.adropofliquid.tmusic.views.mylibrary.artist_view.ArtistViewFragment;
import com.adropofliquid.tmusic.views.mylibrary.MyLibraryFragment;
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.uncat.items.LastPlayedStateItem;
import com.adropofliquid.tmusic.uncat.items.SongItem;
import com.adropofliquid.tmusic.player.PlayerService;
import com.adropofliquid.tmusic.data.queue.Queue;
import com.adropofliquid.tmusic.views.fragment.main.TempSongsFragment;
import com.adropofliquid.tmusic.views.mylibrary.album_songs.AlbumSongsListFragment;
import com.adropofliquid.tmusic.views.nowplaying.NowPlaying;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    public static final int ALBUM_SONGS_LIST_VIEW = 3;
    public static final int ARTIST_LIST_VIEW = 4;
    public static final int SONG_LIST_VIEW = 5;
    private static final String TAG = "MainActivity: ";

    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController = null;

    private ImageView bottomPlayerImage;
    private TextView bottomPlayerTitle;
    private TextView bottomPlayerArtist;
    private ProgressBar progressBar;
    private ImageButton bottomPlayerPrev;
    private ImageButton bottomPlayerPlay;
    private ImageButton bottomPlayerNext;
    private boolean enableBottomButtons;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        TODO dis needs to be done to load song into cache

        Executor executor = ((App)getApplicationContext()).getExecutor();
        SongRepository songRepository = new SongRepository(executor);
        songRepository.updateCache(this);*/



        loadMusicPlayer(savedInstanceState);
    }

    private void loadMusicPlayer(Bundle savedInstanceState) {

        SystemPermissions permissions = new SystemPermissions(this, getSupportFragmentManager());
        if(!permissions.storageIsPermitted()){
            permissions.displayRequestPermission();
        }
        else{
            initializeViews(savedInstanceState);
            initializeMediaBrowser();
        }
    }

    private void initializeViews(Bundle savedInstanceState){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_library);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        tabLayout = findViewById(R.id.library_tabs);

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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, MyLibraryFragment.class, null)
                    .commit();
        }
    }


    public void replaceFragment(int which, long details){
        Fragment fragment;

        switch (which){
            case ALBUM_SONGS_LIST_VIEW:
                fragment = new AlbumSongsListFragment(details);
                break;
            case SONG_LIST_VIEW:
                fragment = new TempSongsFragment(details);
                break;
            default:
                fragment = new ArtistViewFragment(details);
                break;

        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack("")
                .commit();

        tabLayout.setVisibility(View.GONE);
    }

    public void showTabLayout(){
        tabLayout.setVisibility(View.VISIBLE);
    }

    private void initializeMediaBrowser(){ //creates player Service
        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, PlayerService.class),
                connectionCallbacks,
                null);
    }

    private void buildTransportControls() {

        new LoadLastPlayed().execute();

        mediaController.registerCallback(controllerCallback); //stay in sync
    }

    private class LoadLastPlayed extends AsyncTask<String, Void, String> {
        private LastPlayedStateItem last;
        private SongItem lastSong;

        @Override
        protected String doInBackground(String... strings) {

            Queue queue = new Queue(MainActivity.this);
            queue.loadLastPlayedState();
            last = queue.getLastPlayedState();
            if(last != null)
                lastSong = queue.getSong(last.getId());

            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {

            if(last != null){
                enableBottomButtons = true;
                Glide.with(MainActivity.this)
                        .load(lastSong.getAlbumArtUri())
                        .error(R.drawable.miniplayer_default_album_art)
                        .centerCrop().into(bottomPlayerImage);
                bottomPlayerTitle.setText(lastSong.getTitle());
                bottomPlayerArtist.setText(lastSong.getArtist());

                progressBar.setMax(lastSong.getDuration());
                progressBar.setProgress((int) last.getTimePlayed());
                changePausePlayButtons();

            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadMusicPlayer(null);
        } else {
            new SystemPermissions(this, getSupportFragmentManager()).displayPermissionRationale();
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
                    //bottomPlayerImage.setImageBitmap(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ART));
                    Glide.with(MainActivity.this)
                            .load(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ART))
                            .error(R.drawable.miniplayer_default_album_art)
                            .centerCrop().into(bottomPlayerImage);

                    bottomPlayerTitle.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                    bottomPlayerArtist.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
                    progressBar.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));

                    enableBottomButtons = true;
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
            if(enableBottomButtons) {

                switch (view.getId()) {
                    case R.id.bottom_player:
                        Intent intent = new Intent(getApplicationContext(), NowPlaying.class);
                        startActivity(intent);
                        break;
                    case R.id.bottom_player_prev:
                        mediaController.getTransportControls().skipToPrevious();
                        break;
                    case R.id.bottom_player_play:
                        if (mediaController.getPlaybackState().getState() ==
                            PlaybackStateCompat.STATE_PLAYING)
                        mediaController.getTransportControls().pause();
                    else
                        mediaController.getTransportControls().play();
                        break;
                    case R.id.bottom_player_next:
                        mediaController.getTransportControls().skipToNext();
                        break;
                }
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if(mediaBrowser != null)
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

        if (mediaController != null)
            mediaController.unregisterCallback(controllerCallback);

        if (mediaBrowser != null)
            mediaBrowser.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}