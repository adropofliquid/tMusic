package com.adropofliquid.tmusic;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.room.Room;

import com.adropofliquid.tmusic.db.room.QueueDb;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    public static final String CHANNEL_ID = "Music";
    QueueDb queueDb;
    ExecutorService executorService;

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
        queueDb = Room.databaseBuilder(this, QueueDb.class, "queue").allowMainThreadQueries().build();
        executorService = Executors.newFixedThreadPool(4);

    }

    private void createChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "tMusic", importance);
            channel.setDescription("Music player application");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    public QueueDb getQueueDb() {
        return queueDb;
    }

    public Executor getExecutor(){
        return executorService;
    }


}