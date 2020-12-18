package com.adropofliquid.tmusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.service.PlayerService;


public class NowPlaying extends AppCompatActivity {

    private static final String TAG = "NowPlaying: ";
    private ViewPager2 viewPager2;
    private SeekBar seekBar;

    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, PlayerService.class),
                connectionCallbacks,
                null);


        viewPager2 = findViewById(R.id.viewPager);




        //TODO
        //could keep dis in the metadata
//        viewPager2.setCurrentItem(Queue.getCurrentSongPos(),false);

        ImageView playerPrev = findViewById(R.id.previous);
        playerPrev.setOnClickListener(new PlayerOnclickListener());
        ImageView playerPlay = findViewById(R.id.play);
        playerPlay.setOnClickListener(new PlayerOnclickListener());
        ImageView playerNext = findViewById(R.id.next);
        playerNext.setOnClickListener(new PlayerOnclickListener());

        seekBar = findViewById(R.id.seekBar);
        //initially


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

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        stopService(new Intent(this,PlayerService.class));
//    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    // Get the token for the MediaSession
                    MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

                    mediaController = new MediaControllerCompat(NowPlaying.this, // Context
                            token);

                    // Save the controller
                    MediaControllerCompat.setMediaController(NowPlaying.this, mediaController);

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
                    //do some UI shii to change view if metadata is not empty
                    Log.d(TAG,"Meta Changed to: "+metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));

                    //update mini player
//                    bottomPlayerImage.setImageBitmap(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ART));
//                    bottomPlayerTitle.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
//                    bottomPlayerArtist.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));

                    //if it's not already on the song
                    viewPager2.setCurrentItem((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER));

                    int totalDuration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                    seekBar.setMax(totalDuration);
                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    seekBar.setProgress((int) state.getPosition());
                }

            };

    private void buildTransportControls() {

        MediaMetadataCompat metadata = mediaController.getMetadata();
        seekBar.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));

        viewPager2.setAdapter(new NowPlayingAdapter(this));
        viewPager2.setCurrentItem((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER),false);

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);

    }

    private class PlayerOnclickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.previous:
                    mediaController.getTransportControls().skipToPrevious();
                    break;
                case R.id.play:
                    if(!(mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING))
                        mediaController.getTransportControls().play();
                    else
                        mediaController.getTransportControls().pause();
                    break;
                case R.id.next:
                    mediaController.getTransportControls().skipToNext();
                    break;
            }
        }
    }

}