package com.adropofliquid.tmusic.player;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import java.util.List;


////BASED ON https://developer.android.com/guide/topics/media-apps/audio-app/building-a-mediabrowserservice
public class PlayerService extends MediaBrowserServiceCompat {
    private static final String TAG = "Player Service: ";
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private static final float MEDIA_VOLUME_DEFAULT = 1.0f;
    private static final float MEDIA_VOLUME_DUCK = 0.2f;

    private MusicNotification notification;
    private MediaSessionCompat mediaSession;
    private AudioNoiseReciever noiseReciever;

    private PlayerHandler playerHandler;
    private HandlerThread thread;

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

        // MySessionCallback() has methods that handle callbacks from a media controller
        MediaSessionCallback mediaSessionCallback = new MediaSessionCallback();
        mediaSession.setCallback(mediaSessionCallback);

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());

        //initialize noise reciever
        noiseReciever = new AudioNoiseReciever(this, mediaSession.getController());

        notification = new MusicNotification(mediaSession, this);

        thread = new HandlerThread("PlayerThread");
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        Looper serviceLooper = thread.getLooper();
        playerHandler = new PlayerHandler(serviceLooper,
                getApplicationContext(),
                mediaSession);

        Log.d(TAG, "Service Created");

        mediaSession.getController().registerCallback(new MediaControllerCompat.Callback() {
            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                notification.show();
            }
        });
    }

    @Override
    public void onDestroy() {
        mediaSession.getController().getTransportControls().stop();
        super.onDestroy();
        thread.quit();
        stopSelf();
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

    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        private void tellPlayerTo(int doWhat){
            Message msg = playerHandler.obtainMessage();
            msg.what = doWhat;
            playerHandler.sendMessage(msg);
        }

        private void tellPlayerTo(int doWhat, int andWhat){

            Message msg = playerHandler.obtainMessage();
            msg.arg1 = doWhat;
            msg.what = andWhat;
            playerHandler.sendMessage(msg);
        }

        public void setVolume(float volume) {
            Message msg = playerHandler.obtainMessage();
            msg.obj = volume;
            msg.what = PlayerHandler.SET_VOLUME;
            playerHandler.sendMessage(msg);
        }

        @Override
        public void onPlay() {
            if (requestAudioFocus()) {
                // Start the service
                startService(new Intent(PlayerService.this, PlayerService.class));

                if (!mediaSession.isActive()) {
                    mediaSession.setActive(true);
                }

                tellPlayerTo(PlayerHandler.PLAY);

                // Register BECOME_NOISY BroadcastReceiver
                noiseReciever.registerAudioNoisyReceiver();
            }
        }

        @Override
        public void onPause(){

            tellPlayerTo(PlayerHandler.PAUSE);
            noiseReciever.unregisterAudioNoisyReceiver();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                stopForeground(Service.STOP_FOREGROUND_DETACH);
            else
                stopForeground(false);
        }

        @Override
        public void onSkipToNext() {
            if(mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_NONE ||
                    mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED)
                tellPlayerTo(PlayerHandler.MOVE_DOWN_QUEUE);
            else
                tellPlayerTo(PlayerHandler.SKIP_TO_NEXT);
        }

        @Override
        public void onSkipToPrevious() {
            if(mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_NONE ||
                    mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED)
                tellPlayerTo(PlayerHandler.MOVE_UP_QUEUE);
            else
                tellPlayerTo(PlayerHandler.SKIP_TO_PREVIOUS);
        }

        @Override
        public void onSkipToQueueItem(long id) {
            startService(new Intent(PlayerService.this, PlayerService.class));
            tellPlayerTo((int)id, PlayerHandler.SKIP_TO_QUEUE_ITEM);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            startService(new Intent(PlayerService.this, PlayerService.class));

            Message msg = playerHandler.obtainMessage();
//            msg.obj = extras.getSerializable("song");
            msg.what = PlayerHandler.PLAY_FROM_MEDIA_URI;
            msg.setData(extras);
            playerHandler.sendMessage(msg);

        }

        @Override
        public void onStop() {

            tellPlayerTo(PlayerHandler.STOP);
            abandonFocus();
            noiseReciever.unregisterAudioNoisyReceiver();
            stopSelf();
            mediaSession.setActive(false);
            stopForeground(false);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {

            switch (action) {
                case "PauseUpdate": //From the Now Playing activity
                    tellPlayerTo(PlayerHandler.STOP_PLAYBACKSTATE_UPDATE);
                    break;
                case "PlayUpdate":
                    tellPlayerTo(PlayerHandler.START_PLAYBACKSTATE_UPDATE);
                    break;
                case "MoveDownQueue":
                    tellPlayerTo(PlayerHandler.MOVE_DOWN_QUEUE);
                    break;
                case "MoveUpQueue":
                    tellPlayerTo(PlayerHandler.MOVE_UP_QUEUE);
                    break;
            }
        }

        @Override
        public void onSeekTo(long pos) {
            tellPlayerTo((int)pos,PlayerHandler.SEEK_TO);
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
/*            if(shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL){
                //shuffle came from songs list
                //clicked shuffle button on songlist
            }
            else if(shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_GROUP){
                OldPlayList.setOrderedQueue(new ArrayList<>(OldPlayList.getQueue()));

                ArrayList<SongItem> songListShuffle = new ArrayList<>(OldPlayList.getQueue());
                SongItem song = OldPlayList.getCurrentSong();
                songListShuffle.remove(OldPlayList.getCurrentSongPos());
                Collections.shuffle(songListShuffle);
                songListShuffle.add(0, song);

                OldPlayList.updateQueue(songListShuffle);
                OldPlayList.setCurrentSongPos(0);

            }
            else if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE){

                ArrayList<SongItem> songListShuffle = new ArrayList<>(OldPlayList.getOrderedQueue());
                SongItem song = OldPlayList.getCurrentSong();
                OldPlayList.setCurrentSongPos(song.getPosition());
                OldPlayList.updateQueue(songListShuffle);
            }

            mediaSession.setShuffleMode(shuffleMode)*/;
            mediaSession.setShuffleMode(shuffleMode);
            tellPlayerTo(PlayerHandler.SHUFFLE_PLAYQUEUE);
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            mediaSession.setRepeatMode(repeatMode);
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
                        //TODO
                        // Song plays wen call is dropped

                        if (mediaSession.getController().getPlaybackState().getState() != PlaybackStateCompat.STATE_PLAYING) {
                            //onPlay();
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


}
