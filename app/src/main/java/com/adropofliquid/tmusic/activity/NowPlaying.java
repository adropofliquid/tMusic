package com.adropofliquid.tmusic.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ComponentName;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.adapters.NowPlayingAdapter;
import com.adropofliquid.tmusic.adapters.NowPlayingNoAdapter;
import com.adropofliquid.tmusic.service.PlayerService;
import com.adropofliquid.tmusic.service.Queue;



public class NowPlaying extends AppCompatActivity {

    private static final String TAG = "NowPlaying: ";
    private ViewPager2 viewPager2;
    private SeekBar seekBar;
    private TextView durationStart;
    private TextView durationEnd;
    private ImageView playerPrev, playerPlay, playerNext, playerShuffle, playerRepeat;

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
        playerPrev = findViewById(R.id.previous);
        playerPlay = findViewById(R.id.play);
        playerNext = findViewById(R.id.next);
        playerShuffle = findViewById(R.id.shuffle);
        playerRepeat = findViewById(R.id.repeat);
        durationStart = findViewById(R.id.durationStart);
        durationEnd = findViewById(R.id.durationEnd);
        seekBar = findViewById(R.id.seekBar);

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
        ///stopService(new Intent(this,PlayerService.class));
    }

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
                    //if it's not already on the song
                    viewPager2.setCurrentItem((int) metadata
                            .getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER));

                    int totalDuration = (int) metadata
                            .getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                    seekBar.setMax(totalDuration);

                    durationEnd.setText(durationFormat(totalDuration));

                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    seekBar.setProgress((int) state.getPosition());
                    durationStart.setText(durationFormat((int) state.getPosition()));

                    changePausePlayButton();
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                    changeRepeatButtons();
                }

                @Override
                public void onShuffleModeChanged(int shuffleMode) {
                    changeShuffleButton();
                    viewPager2.getAdapter().notifyDataSetChanged();
                    viewPager2.setCurrentItem(Queue.getCurrentSongPos(),false);
                }
            };

    private void buildTransportControls() {

        if(!Queue.isEmpty()){
            playerPrev.setOnClickListener(new PlayerOnclickListener());
            playerPlay.setOnClickListener(new PlayerOnclickListener());
            playerNext.setOnClickListener(new PlayerOnclickListener());
            playerShuffle.setOnClickListener(new PlayerOnclickListener());
            playerRepeat.setOnClickListener(new PlayerOnclickListener());

            changePausePlayButton();
            changeRepeatButtons();
            changeShuffleButton();

            viewPager2.setAdapter(new NowPlayingAdapter(this));
            viewPager2.setCurrentItem(Queue.getCurrentSongPos(), false);

            seekBar.setMax(Queue.getCurrentSong().getDuration());

            seekBar.setProgress(Queue.getSongProgress());

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    durationStart.setText(durationFormat(progress));
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    String action = "PauseUpdate";
                    mediaController.getTransportControls().sendCustomAction(action, new Bundle());
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    String action = "PlayUpdate";
                    int position = seekBar.getProgress();
                    mediaController.getTransportControls().seekTo(position);
                    mediaController.getTransportControls().sendCustomAction(action, new Bundle());
                }
            });

            durationStart.setText(durationFormat(Queue.getSongProgress()));
            durationEnd.setText(durationFormat(Queue.getCurrentSong().getDuration()));

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrollStateChanged(int state) {
                    if(state == ViewPager2.SCROLL_STATE_IDLE){
                        if(Queue.getCurrentSongPos() != viewPager2.getCurrentItem()){
                            mediaController.getTransportControls().skipToQueueItem(viewPager2.getCurrentItem());
                        }

                    }
                }
            });

        }
        else {
            viewPager2.setAdapter(new NowPlayingNoAdapter(this));
        }
        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);
    }

    private void changePausePlayButton(){
        if((mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING))
            playerPlay.setImageResource(R.drawable.widget_pause_normal);
        else
            playerPlay.setImageResource(R.drawable.widget_play_normal);
    }

    private void changeRepeatButtons(){
        if(mediaController.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_NONE){
            playerRepeat.setImageResource(R.drawable.playerview_repeat_off);
        }
        else if(mediaController.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ALL){
            playerRepeat.setImageResource(R.drawable.playerview_repeat_all);
        }
        else{
            playerRepeat.setImageResource(R.drawable.playerview_repeat_one);
        }
    }

    private void changeShuffleButton(){
        if(mediaController.getShuffleMode() == PlaybackStateCompat.SHUFFLE_MODE_NONE){
            playerShuffle.setImageResource(R.drawable.playerview_shuffle_off);
        }
        else{
            playerShuffle.setImageResource(R.drawable.playerview_shuffle_on);
        }
    }

    private class PlayerOnclickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.previous:
                    if(mediaController.getPlaybackState().getPosition() > 10000){
                        mediaController.getTransportControls().seekTo(0);
                    }
                    else{
                        viewPager2.setCurrentItem(viewPager2.getCurrentItem()-1);
                    }
                    break;
                case R.id.play:
                    if(!(mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING))
                        mediaController.getTransportControls().play();
                    else
                        mediaController.getTransportControls().pause();
                    break;
                case R.id.next:
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
                    break;
                case R.id.shuffle:
                    if(mediaController.getShuffleMode() == PlaybackStateCompat.SHUFFLE_MODE_NONE){
                        mediaController.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_GROUP);
                        Toast.makeText(NowPlaying.this,"Shuffle On",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mediaController.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                        Toast.makeText(NowPlaying.this,"Shuffle Off",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.repeat:
                    if(mediaController.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_NONE){
                        //switch to repeat all
                        mediaController.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                        Toast.makeText(NowPlaying.this,"Repeat All",Toast.LENGTH_SHORT).show();
                    }
                    else if(mediaController.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ALL){
                        //switch to repeat one
                        mediaController.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                        Toast.makeText(NowPlaying.this,"Repeat",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mediaController.getTransportControls().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                        Toast.makeText(NowPlaying.this,"Repeat Off",Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

  /*  private String durationFormat(int duration){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }*/

    private String durationFormat(int milliSeconds) {
        String finalTimerString = "";
        String secondsString;

        //Converting total duration into time
        int hours =  (milliSeconds / 3600000);
        int minutes = (milliSeconds % 3600000) / 60000;
        int seconds = ((milliSeconds % 3600000) % 60000 / 1000);

        // Adding hours if any
        if (hours > 0)
            finalTimerString = hours + ":";

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // Return timer String;
        return finalTimerString;
    }
}