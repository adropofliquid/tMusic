package com.adropofliquid.tmusic.player;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.adropofliquid.tmusic.items.SongItem;

import java.io.IOException;


public class PlayerHandler extends Handler {

    public static final int SKIP_TO_QUEUE_ITEM = 1;
    public static final int PLAY = 2;
    public static final int PAUSE = 3;
    public static final int STOP = 4;
    public static final int SEEK_TO = 5;
    public static final int SKIP_TO_NEXT = 6;
    public static final int SKIP_TO_PREVIOUS = 7;
    public static final int STOP_PLAYBACKSTATE_UPDATE = 8;
    public static final int START_PLAYBACKSTATE_UPDATE = 9;
    public static final int SET_VOLUME = 10;
    private static final String TAG = "Player Handler";
    private SongItem currentSong;
    private final Context context;
    private final Queue queue;
    private MediaPlayer mediaPlayer;
    private final MediaSessionCompat mediaSession;


    public PlayerHandler(Looper looper, Context context, MediaSessionCompat mediaSession) {
        super(looper);
        this.context = context;
        this.queue = new Queue(context);
        this.mediaSession = mediaSession;

    }

    @Override
    public void handleMessage(Message msg) {

        switch(msg.what){
            case SKIP_TO_QUEUE_ITEM:
                onSkipToQueueItem(msg.arg1);
                break;
            case PLAY:
                onPlay();
                break;
            case PAUSE:
                onPause();
                break;
            case STOP:
                onStop();
                break;
            case SEEK_TO:
                onSeekTo(msg.arg1);
                break;
            case SKIP_TO_NEXT:
                onSkipToNext();
                break;
            case SKIP_TO_PREVIOUS:
                onSkipToPrevious();
                break;
            case STOP_PLAYBACKSTATE_UPDATE:
                stopPlaybackStateUpdate();
                break;
            case START_PLAYBACKSTATE_UPDATE:
                updateCurrentPosition();
                break;
            case SET_VOLUME:
                setVolume((Float) msg.obj);
                break;
        }

    }

    private void onPlay() {
        if((mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED)
                && (mediaPlayer != null)){
            setNewState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition());
            setNewMetadata(currentSong,mediaPlayer.getDuration(), currentSong.getPosition());
            mediaPlayer.start();
            updateCurrentPosition();
        }
        else{
            startPlayback();
        }
    }

    private void onPause() {
        setNewState(PlaybackStateCompat.STATE_PAUSED,mediaPlayer.getCurrentPosition());
        setNewMetadata(currentSong,mediaPlayer.getDuration(), currentSong.getPosition());
        mediaPlayer.pause();
        stopPlaybackStateUpdate();
    }

    private void onStop() {
        stopPlaybackStateUpdate();
        stopReleaseMediaPlayer(PlaybackStateCompat.STATE_STOPPED);
    }

    private void onSkipToNext() {

        if(mediaSession.getController().getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ONE){
            //play the current song again
            mediaPlayer.seekTo(0);
            setNewState(PlaybackStateCompat.STATE_PAUSED, 0);
            onPlay();
        }

        if(queue.hasNext()){
            onSkipToQueueItem(currentSong.getPosition()+ 1);
        }
        else{
            if(mediaSession.getController().getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ALL){
                onSkipToQueueItem(0); //go to first song
            }
            else{
                //stopReleaseMediaPlayer();
                onStop();
            }
        }

    }

    private void onSkipToPrevious() {
        if(queue.hasPrev()){
            onSkipToQueueItem(currentSong.getPosition() - 1);

        }
        else {
//                stopReleaseMediaPlayer();
            onStop();
        }
    }

    private void onSeekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    private void stopPlaybackStateUpdate() {
        removeCallbacksAndMessages(null);
    }

    private void onSkipToQueueItem(int pos) {
        setNewState(PlaybackStateCompat.STATE_PLAYING, 0);
        currentSong = queue.getCurrentSong(pos);
        //FIXME handler error of player not found
        startPlayback();
    }

    private void startPlayback() {

        stopReleaseMediaPlayer(PlaybackStateCompat.STATE_PLAYING);

        mediaPlayer = MediaPlayer.create(context, currentSong.getUri());
        //mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build());
        //mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(4.0f)); //
        mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaSession.getController().getTransportControls().skipToNext());

        mediaPlayer.start();
        mediaPlayer.seekTo((int) mediaSession.getController().getPlaybackState().getPosition());
        updateCurrentPosition(); // update state and Position for seekbar and progressbar
        setNewMetadata(currentSong,mediaPlayer.getDuration(), currentSong.getPosition());

        Log.d(TAG, currentSong.getTitle()+" is Playing");
    }

    private void stopReleaseMediaPlayer(int state) {

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            Log.d(TAG," A player was released");
            mediaPlayer = null;
            setNewState(state,0);
        }
    }

    private void updateCurrentPosition() {
        if (mediaPlayer == null) {
            return;
        }
        postDelayed(() -> {
            int currentPosition = mediaPlayer.getCurrentPosition();
            setNewState(PlaybackStateCompat.STATE_PLAYING,currentPosition);
            updateCurrentPosition();
        }, 1000);
    }

    private void setNewMetadata(SongItem songItem,long duration, long positionInQueue) {

        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,songItem.getArtist());
        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songItem.getAlbum());
        try {
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, MediaStore.Images.Media.getBitmap(context.getContentResolver(),songItem.getAlbumArtUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, songItem.getTitle());
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,duration);
        builder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, positionInQueue);

        mediaSession.setMetadata(builder.build());
        Log.d(TAG,"metadata: "+songItem.getTitle());

        String action = "SetNotification";
        mediaSession.getController().getTransportControls().sendCustomAction(action, new Bundle());

    }

    private void setNewState(@PlaybackStateCompat.State int newState, long playPosition) {

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_STOP);

        stateBuilder.setState(newState, playPosition, 1.0f);

        mediaSession.setPlaybackState(stateBuilder.build());

    }

    private void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

}
