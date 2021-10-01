package com.adropofliquid.tmusic.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;

public class AudioNoiseReciever {

    private Context context;
    private MediaControllerCompat mediaController;

    private static final IntentFilter AUDIO_NOISY_INTENT_FILTER = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private boolean audioNoisyReceiverRegistered = false;

    private final BroadcastReceiver audioNoisyReceiver;

    public AudioNoiseReciever(Context context, final MediaControllerCompat mediaController) {
        this.context = context;
        this.mediaController = mediaController;

        audioNoisyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                    mediaController.getTransportControls().pause();
                }
            }
        };
    }

    public void registerAudioNoisyReceiver() {
        if (!audioNoisyReceiverRegistered) {
            context.registerReceiver(audioNoisyReceiver, AUDIO_NOISY_INTENT_FILTER);
            audioNoisyReceiverRegistered = true;
            Log.d("Noise Reciever: ","Registered");
        }
    }

    public void unregisterAudioNoisyReceiver() {
        if (audioNoisyReceiverRegistered) {
            context.unregisterReceiver(audioNoisyReceiver);
            audioNoisyReceiverRegistered = false;
            Log.d("Noise Reciever: ","Registered");
        }
    }
}
