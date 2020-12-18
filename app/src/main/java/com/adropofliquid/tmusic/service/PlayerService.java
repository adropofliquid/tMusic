package com.adropofliquid.tmusic.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.activity.NowPlaying;
import com.adropofliquid.tmusic.items.SongItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


////BASED ON https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice
public class PlayerService extends MediaBrowserServiceCompat {
    private static final String TAG = "Player Service: ";
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private static final float MEDIA_VOLUME_DEFAULT = 1.0f;
    private static final float MEDIA_VOLUME_DUCK = 0.2f;
    private static final int REQUEST_CODE = 550;

    public static final int Notification_ID = 1;
    public static final String QUEUE_KEY = "queue_key" ;
    public static final String QUEUE_START_KEY = "queue_start_key" ;

    private MediaSessionCompat mediaSession;
    private AudioNoiseReciever noiseReciever;

    private Handler handler;

    private Queue playQueue;

    //TODO maybe create a class
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a MediaSessionCompat
        mediaSession = new MediaSessionCompat(this, "tMusicService");

        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        setNewState(PlaybackStateCompat.STATE_NONE,0);

        // MySessionCallback() has methods that handle callbacks from a media controller
        MediaSessionCallback mediaSessionCallback = new MediaSessionCallback();
        mediaSession.setCallback(mediaSessionCallback);

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());

        //initialize noise reciever
        noiseReciever = new AudioNoiseReciever(this, mediaSession.getController());

        handler = new Handler();

        playQueue = new Queue();

        Log.d(TAG, "Service Created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stop player and save states
        mediaSession.getController().getTransportControls().stop();
        stopSelf();
    }

    private void startNotification()  {
        MediaDescriptionCompat description = mediaSession.getController().getMetadata().getDescription();

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setLargeIcon(description.getIconBitmap())
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setContentIntent(createContentIntent())
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)

                //swipe to end service
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_STOP))

                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_baseline_skip_previous_24, "Previous",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_baseline_play_arrow_24, "Play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_PLAY_PAUSE)))
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_baseline_skip_next_24, "Next",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT)))

                // Take advantage of MediaStyle features
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0,1,2)

                        // Add a cancel button
                        .setShowCancelButton(false))
                .build();
        startForeground(Notification_ID, notification);
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(this, NowPlaying.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                this, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        //  Browsing not allowed
        /*if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentMediaId)) {
            result.sendResult(null);
            return;
        }

        // Assume for example that the music catalog is already loaded/cached.

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        // Check if this is the root menu:
        if (MY_MEDIA_ROOT_ID.equals(parentMediaId)) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }*/
        result.sendResult(null);

    }

    private void setNewState(@PlaybackStateCompat.State int newState, long playPosition) {
        //TODO
        // setActions should be based on current playstate
        //switch(get formerState)
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_STOP);

        stateBuilder.setState(newState, playPosition, 1.0f);

        mediaSession.setPlaybackState(stateBuilder.build());

       /* String stateName = "None";
        switch (newState){
            case PlaybackStateCompat.STATE_PAUSED:
                stateName = "Paused";
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                stateName = "Playing";
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                stateName = "Stopped";
                break;
            default:
                break;
        }*/

        //Log.d(TAG, "PlaybackState is: "+ stateName);
    }

    private void setNewMetadata(SongItem songItem,long duration, long positionInQueue) {

        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,songItem.getArtist());
        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songItem.getAlbum());
        try {
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, MediaStore.Images.Media.getBitmap(getContentResolver(),songItem.getAlbumArtUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, songItem.getTitle());
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,duration);
        builder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, positionInQueue);

        mediaSession.setMetadata(builder.build());
        Log.d(TAG,"metadata: "+songItem.getTitle());
    }

    private void stopReleaseMediaPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            Log.d(TAG," A player was released");
            mediaPlayer = null;
            setNewState(PlaybackStateCompat.STATE_STOPPED,0);
        }
    }

    private void startPlayback() {
        //onPlay,onNext, onPrev called u

        //stop and null the static Player
        stopReleaseMediaPlayer();

        //TODO
        // maybe check if d song exists before creating
        // skip to next if song don't exist
        // on error try to play next song

        mediaPlayer = MediaPlayer.create(this,playQueue.getCurrentSong().getSongUri());
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build());
        //mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(4.0f)); //

        mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaSession.getController().getTransportControls().skipToNext());

        mediaPlayer.start();
        mediaPlayer.seekTo((int) mediaSession.getController().getPlaybackState().getPosition());
        //setNewState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition());
        updateCurrentPosition(); // for seekbar and progressbar
        setNewMetadata(playQueue.getCurrentSong(),mediaPlayer.getDuration(), playQueue.getCurrentSongPos());

        Log.d(TAG,playQueue.getCurrentSong().getTitle()+" is Playing");
    }

    private void updateCurrentPosition() {
        if (mediaPlayer == null) {
            return;
        }
        handler.postDelayed(() -> {
            int currentPosition = mediaPlayer.getCurrentPosition();
            setNewState(PlaybackStateCompat.STATE_PLAYING,currentPosition);
            updateCurrentPosition();
        }, 1000);
    }

    private void stopPlaybackStateUpdate() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    private class MediaSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {

            if (requestAudioFocus()) {
                // Start the service
                startService(new Intent(PlayerService.this, PlayerService.class));

                if (!mediaSession.isActive()) {
                    mediaSession.setActive(true);
                }

                // start the player
                //paused and d mediaplayer != null
                if((mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED)
                        && (mediaPlayer != null)){
                    setNewState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition());
                    mediaPlayer.start();
                    updateCurrentPosition();
                }
                else{
                    startPlayback();
                }

                // Register BECOME_NOISY BroadcastReceiver
                noiseReciever.registerAudioNoisyReceiver();

                // Put the service in the foreground, post notification
                startNotification();
            }
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {

            stopReleaseMediaPlayer();

            ArrayList<SongItem> songList = extras.getParcelableArrayList(QUEUE_KEY);

            playQueue.setQueue(songList);
            playQueue.setCurrentSongPos(Integer.parseInt(mediaId));

            onPlay();

            mediaSession.setExtras(extras);
        }

        @Override
        public void onPause(){
            Log.d(TAG,"Pause was Called");
            setNewState(PlaybackStateCompat.STATE_PAUSED,mediaPlayer.getCurrentPosition());
            //setNewMetadata(playQueue.getCurrentSong());
            mediaPlayer.pause();
            stopPlaybackStateUpdate();
            noiseReciever.unregisterAudioNoisyReceiver();
            stopForeground(false);

        }

        @Override
        public void onSkipToNext() {
            Log.d(TAG, " Next Clicked");
            //not last song
            if(playQueue.hasNext()){
                playQueue.setNewPos(playQueue.getCurrentSongPos() + 1);

                startPlayback();
                startNotification();//so notification can change wen song changes
            }
            else{
//                stopReleaseMediaPlayer();
                onStop();
            }
        }

        @Override
        public void onSkipToPrevious() {
            if(playQueue.hasPrev()){// it has previous
                //TODO logic to restart if playPosition is > 0
                playQueue.setNewPos(playQueue.getCurrentSongPos() - 1);
                startPlayback();
                startNotification();
            }
            else {
//                stopReleaseMediaPlayer();
                onStop();
            }
        }

        @Override
        public void onStop() {
            abandonFocus();
            noiseReciever.unregisterAudioNoisyReceiver();
            stopSelf();
            mediaSession.setActive(false);
            stopReleaseMediaPlayer();
            stopForeground(false);
        }

        @Override
        public void onSeekTo(long pos) {
            mediaPlayer.seekTo((int)pos);
        }

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        private boolean requestAudioFocus() {
            final int result = am.requestAudioFocus(afChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }

        private final AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {

            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        //TODO maybe do beta
                        if (mediaSession.getController().getPlaybackState().getState() != PlaybackStateCompat.STATE_PLAYING) {
                            onPlay();
                        } else if (mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                            setVolume(MEDIA_VOLUME_DEFAULT);
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        setVolume(MEDIA_VOLUME_DUCK);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        if (mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                            onPause();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        am.abandonAudioFocus(this);
                        //stop();
                        onPause();
                        break;
                }
            }
        };

        private void abandonFocus() {
            am.abandonAudioFocus(afChangeListener);
        }

    }

    private static class Queue {

        private ArrayList<SongItem> queue = null;
        private int currentSongPos = 0;
        private final String TAG = "Queue: ";


        public void setQueue(ArrayList<SongItem> queue) {
            this.queue = queue;
        }

        public ArrayList<SongItem> getQueue() {
            return queue;
        }

        public int getCurrentSongPos() {
            return currentSongPos;
        }

        public void setCurrentSongPos(int position) {
            this.currentSongPos = position;
        }

        public SongItem getCurrentSong() {
            return queue.get(currentSongPos);
        }

        public boolean hasNext(){
            return getCurrentSongPos() + 1 < queue.size();
        }

        public boolean hasPrev(){
            return getCurrentSongPos() != 0;
        }

        public void setNewPos(int position) {
            this.currentSongPos = position;
        }
    }
}
