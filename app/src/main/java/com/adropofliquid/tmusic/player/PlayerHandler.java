package com.adropofliquid.tmusic.player;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.adropofliquid.tmusic.items.SongItem;
import com.adropofliquid.tmusic.queue.Queue;

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
    public static final int PLAY_FROM_LAST_PLAYED = 11;
    public static final int MOVE_DOWN_QUEUE = 12;
    public static final int MOVE_UP_QUEUE = 13;
    private static final String TAG = "Player Handler";
    private final Context context;
    private final Queue queue;
    private MediaPlayer mediaPlayer;
    private final MediaSessionCompat mediaSession;

    public PlayerHandler(Looper looper, Context context, MediaSessionCompat mediaSession) {
        super(looper);
        this.context = context;
        this.queue = new Queue(context);
        this.mediaSession = mediaSession;

        setNewState(PlaybackStateCompat.STATE_NONE,0);
        prepareLastPlayed();
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
                updateAndSaveCurrentPosition();
                break;
            case SET_VOLUME:
                setVolume((Float) msg.obj);
                break;
            case PLAY_FROM_LAST_PLAYED:
                onPlayFromLast(msg.arg1);
                break;
            case MOVE_DOWN_QUEUE:
                moveDownQueue();
                break;
            case MOVE_UP_QUEUE:
                moveUpQueue();
                break;
        }

    }

    @SuppressLint("WrongConstant")
    private void prepareLastPlayed(){
        post(() -> {
            //load last song from db
            queue.loadLastPlayedState();
            if(queue.getLastPlayedState() != null) { //might fix first launch problem

                queue.setCurrentSongWithId(queue.getLastPlayedState().getId());

                if(queue.getLastPlayedState().isShuffled())
                    mediaSession.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);

                mediaSession.setRepeatMode(queue.getLastPlayedState().getRepeat());

                setNewMetadata();
            }
        });
    }

    private void onPlay() {

        if(mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_NONE ||
                mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED){

            setNewState(PlaybackStateCompat.STATE_PLAYING,
                    queue.getLastPlayedState().getTimePlayed());

            if(mediaPlayer != null){
                setNewState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition());
                setNewMetadata();
                mediaPlayer.start();
                updateAndSaveCurrentPosition();
            }
            else
                startPlayback();
        }
        else{
            startPlayback();
        }
    }

    private void onPause() {

        setNewState(PlaybackStateCompat.STATE_PAUSED,mediaPlayer.getCurrentPosition());
        setNewMetadata();
        mediaPlayer.pause();
        stopPlaybackStateUpdate();
    }

    private void onStop() {
        stopPlaybackStateUpdate();
        stopReleaseMediaPlayer(PlaybackStateCompat.STATE_STOPPED);
    }

    private void onSkipToNext() {

        if(mediaSession.getController().getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ONE){
            repeatSong();
        }
        else{
            playNextOrRepeatAll();
        }

    }

    private void playNextOrRepeatAll(){

        if(isSessionShuffling()){
            if (queue.hasNextOnPlayOrder())
                onSkipToQueueItem(queue.getCurrentSong().getPlayOrder() + 1);
            else
                repeatAll();
        }
        else{
            if(queue.hasNext()) {
                playNext();
            }
            else {
                repeatAll();
            }
        }
    }

    private void playNext(){
        moveDownQueue();
        onPlay();
    }

    private void repeatSong(){
         //play the current song again
            mediaPlayer.seekTo(0);
            setNewState(PlaybackStateCompat.STATE_PAUSED, 0);
            onPlay();
    }

    private void repeatAll(){
        if(mediaSession.getController().getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ALL)
            onSkipToQueueItem(0); //go to first song
        else
            onStop();
    }

    private void onSkipToPrevious() {

        if(isSessionShuffling()) {
            if (queue.hasPrevOnPlayOrder())
                onSkipToQueueItem(queue.getCurrentSong().getPlayOrder() - 1);
        }
        else {
            if(queue.hasPrev()){
                moveUpQueue();
                onPlay();
            }
            else {
                onStop();
            }
        }

    }

    private void onSeekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    private void stopPlaybackStateUpdate() {
        removeCallbacksAndMessages(null);
    }

    private void onSkipToQueueItem(int id) {
        setNewState(PlaybackStateCompat.STATE_PLAYING, 0);

        if(isSessionShuffling()){
            queue.setCurrentSongWithPlayOrder(id);
        }
        else{
            //FIXME abi na TODO error of music not found
            queue.setCurrentSongWithId(id);
        }

        mediaSession.getController().getTransportControls().play();



    }

    private void onPlayFromLast(int id) {

        setNewState(PlaybackStateCompat.STATE_PLAYING,
                queue.getLastPlayedState().getTimePlayed());

        queue.setCurrentSongWithId(id);
        mediaSession.getController().getTransportControls().play();
    }

    private void startPlayback() {

        stopReleaseMediaPlayer(PlaybackStateCompat.STATE_PLAYING);

        makeNewPlayer();

        mediaPlayer.start();
        mediaPlayer.seekTo((int) mediaSession.getController().getPlaybackState().getPosition());
        updateAndSaveCurrentPosition();
        setNewMetadata();

        Log.d(TAG, queue.getCurrentSong().getTitle()+" is Playing");

    }

    private void makeNewPlayer(){
        mediaPlayer = MediaPlayer.create(context, queue.getCurrentSong().getUri());
        //mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build());
        //mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(4.0f)); //
        mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaSession.getController().getTransportControls().skipToNext());

        // FIXME experimental
        //  on error fix
        //  media was not found maybe
        //  so go to next

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {

            onStop();
            mp.release();
            onSkipToNext();
            return false;
        });
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

    private void updateAndSaveCurrentPosition() { // update state and Position for seekbar and progressbar
        if (mediaPlayer == null) {
            return;
        }
        postDelayed(() -> {
            int currentPosition = mediaPlayer.getCurrentPosition();
            setNewState(PlaybackStateCompat.STATE_PLAYING,currentPosition);
            saveLastState();
            updateAndSaveCurrentPosition();
        }, 1000);

    }

    private void setNewMetadata() {

        SongItem currentSong = queue.getCurrentSong();

        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST,currentSong.getArtist());
        builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentSong.getAlbum());
        try {
            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, MediaStore.Images.Media.getBitmap(context.getContentResolver(),currentSong.getAlbumArtUri()));
        } catch (IOException e) {

            //TODO replace wit placeholder
            e.printStackTrace();
        }
        builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.getTitle());
        builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION,currentSong.getDuration());
        builder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, currentSong.getId());

        mediaSession.setMetadata(builder.build());
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

    private void saveLastState() {
        queue.saveLastPlayedState(mediaPlayer.getCurrentPosition(),
                isSessionShuffling(),
                mediaSession.getController().getRepeatMode());
    }

    private boolean isSessionShuffling() {
        return mediaSession.getController().getShuffleMode() != PlaybackStateCompat.SHUFFLE_MODE_NONE;
    }

    private void moveDownQueue() { //could change to making queue Class do it
        if(queue.hasNext()){
            queue.setCurrentSongWithId(queue.getCurrentSong().getId() + 1);
            queue.saveLastPlayedState(0,
                    isSessionShuffling(),
                    mediaSession.getController().getRepeatMode());
        }
        stopReleaseMediaPlayer(PlaybackStateCompat.STATE_PAUSED);
        setNewMetadata();
        //FIXME maybe useless
        setNewState(PlaybackStateCompat.STATE_PAUSED, 0);
        stopPlaybackStateUpdate();
    }

    private void moveUpQueue() {
        if(queue.hasPrev()){
            queue.setCurrentSongWithId(queue.getCurrentSong().getId() - 1);
            queue.saveLastPlayedState(0,
                    isSessionShuffling(),
                    mediaSession.getController().getRepeatMode());
        }
        stopReleaseMediaPlayer(PlaybackStateCompat.STATE_PAUSED);
        setNewMetadata();
        setNewState(PlaybackStateCompat.STATE_PAUSED, 0);
        stopPlaybackStateUpdate();
    }
}
