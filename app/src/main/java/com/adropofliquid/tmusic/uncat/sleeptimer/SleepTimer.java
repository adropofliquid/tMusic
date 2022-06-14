package com.adropofliquid.tmusic.uncat.sleeptimer;

import android.support.v4.media.session.MediaControllerCompat;

import java.util.Timer;
import java.util.TimerTask;

public class SleepTimer {

    private MediaControllerCompat mediaControllerCompat;
    Timer timer;

    public SleepTimer(int seconds, MediaControllerCompat mediaController) {
        this.mediaControllerCompat = mediaController;
        timer = new Timer();
        timer.schedule(new SleepTask(), seconds*60*1000);
    }

    class SleepTask extends TimerTask {
        public void run() {
            mediaControllerCompat.getTransportControls().stop();
            timer.cancel(); //Terminate the timer thread
        }
    }
}
