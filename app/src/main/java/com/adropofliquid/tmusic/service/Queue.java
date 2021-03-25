package com.adropofliquid.tmusic.service;

import com.adropofliquid.tmusic.items.SongItem;

import java.util.ArrayList;
import java.util.Collections;

public class Queue {

    private static ArrayList<SongItem> queue = null;
    private static ArrayList<SongItem> orderedQueue = null;
    private static int currentSongPos = 0;
    private static int currentPos = 0;
    private final String TAG = "Queue: ";



    public static void setQueue(ArrayList<SongItem> queue) {
        Queue.queue = queue;
    }

    public static void updateQueue(ArrayList<SongItem> updatedQueue){
        queue.clear();
        queue.addAll(updatedQueue);
    }

    public static ArrayList<SongItem> getQueue() {
        return queue;
    }

    public static int getCurrentSongPos() {
        return currentSongPos;
    }

    public static void setCurrentSongPos(int position) {
        Queue.currentSongPos = position;
    }

    public static SongItem getCurrentSong() {
        return queue.get(currentSongPos);
    }

    public static boolean hasNext(){
        return getCurrentSongPos() + 1 < queue.size();
    }

    public static boolean hasPrev(){
        return getCurrentSongPos() != 0;
    }

    public static ArrayList<SongItem> getOrderedQueue() {
        return orderedQueue;
    }

    public static void setOrderedQueue(ArrayList<SongItem> orderedQueue) {
        Queue.orderedQueue = orderedQueue;
    }

    public static boolean isEmpty() {
        return queue == null || queue.isEmpty();
    }

    public static void setSongProgress(int currentPosition) {
        currentPos = currentPosition;
    }

    public static int getSongProgress() {
        return currentPos;
    }

}