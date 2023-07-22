package com.adropofliquid.tmusic.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.views.MainActivity;
import com.adropofliquid.tmusic.views.nowplaying.NowPlaying;

public class MusicNotification {

    private final Context context;
    private final MediaSessionCompat mediaSession;
    private final Service service;
    public static final int Notification_ID = 1;
    private static final int REQUEST_CODE = 550;

    public MusicNotification(MediaSessionCompat mediaSession, Service service){
        this.mediaSession = mediaSession;
        this.service = service;
        this.context = service.getApplicationContext();
    }

    public void show()  {
        service.startForeground(Notification_ID, buildNotification());
    }

    private Notification buildNotification()  {
        MediaDescriptionCompat description = mediaSession.getController().getMetadata().getDescription();

        NotificationCompat.Action pause = new NotificationCompat.Action(
                R.drawable.ic_baseline_pause_24, "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_PAUSE));

        NotificationCompat.Action play = new NotificationCompat.Action(
                R.drawable.ic_baseline_play_arrow_24, "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_PLAY));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, App.CHANNEL_ID)
                .setLargeIcon(description.getIconBitmap())
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setContentIntent(createContentIntent())
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)

                //swipe to end service
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_STOP))

                // Make the transport controls visible on the lockscreen
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_baseline_skip_previous_24, "Previous",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)))


                // Take advantage of MediaStyle features
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2)

                        // Add a cancel button
                        .setShowCancelButton(false));

        if(mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED) {
            builder.addAction(play);
        }
        else {
            builder.addAction(pause);
        }

        builder.addAction(new NotificationCompat.Action(
                R.drawable.ic_baseline_skip_next_24, "Next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));



        return builder.build();
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(context, MainActivity.class); //TODO should be NowPlaying
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                context, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
