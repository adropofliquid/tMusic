package com.adropofliquid.tmusic;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.room.Room;

import com.adropofliquid.tmusic.data.SongRepository;
import com.adropofliquid.tmusic.room.QueueDb;
import com.adropofliquid.tmusic.room.SongRoom;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    public static final String CHANNEL_ID = "Music";
    QueueDb queueDb;
    SongRoom songRoom;
    ExecutorService executorService;
    Handler mainThreadHandler;


    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
        queueDb = Room.databaseBuilder(this, QueueDb.class, "queue").allowMainThreadQueries().fallbackToDestructiveMigration().build();//FIXME maybe disallow mainthread queries
        songRoom = Room.databaseBuilder(this, SongRoom.class, "songs").fallbackToDestructiveMigration().build();
        executorService = Executors.newFixedThreadPool(4);
        mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
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

    public SongRoom getSongRoom(){
        return songRoom;
    }

    public Handler getMainThreadHandler(){
        return mainThreadHandler;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        queueDb.close();
        songRoom.close();
    }
}