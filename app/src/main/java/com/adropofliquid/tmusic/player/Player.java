package com.adropofliquid.tmusic.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.adropofliquid.tmusic.items.SongItem;

public class Player{

    private final Context context;
    private MediaPlayer player;
    private static final String TAG = "Player";
    private SongItem currentSong;
    private MediaSessionCompat mediaSession;

    public Player(Context context, MediaSessionCompat mediaSession){
        this.context = context;
        this.mediaSession = mediaSession;
    }

    public void setCurrentSong(SongItem song){
        this.currentSong = song;
    }

    public void playCurrentSong(int fromPosition){
        stopReleaseMediaPlayer();

        player = MediaPlayer.create(context, currentSong.getUri());
        //mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build());
        //mediaPlayer.setPlaybackParams(new PlaybackParams().setSpeed(4.0f)); //

        player.setOnCompletionListener(mediaPlayer -> mediaSession.getController().getTransportControls().skipToNext());

        player.start();
        player.seekTo(fromPosition);
    }

    private void stopReleaseMediaPlayer() {
        if(player != null) {
            player.stop();
            player.reset();
            player.release();
            Log.d(TAG,currentSong.getTitle()+" player was released");
        }
    }



}
