package com.adropofliquid.tmusic.data.queue;

import com.adropofliquid.tmusic.data.SongRepository;
import com.adropofliquid.tmusic.room.model.LastPlayedStateItem;
import com.adropofliquid.tmusic.uncat.items.SongItem;


public class Queue {

    private SongItem currentSong;
    private LastPlayedStateItem lastPlayedStateItem;
    private final SongRepository songRepository;

    public Queue(SongRepository songRepository){
        this.songRepository = songRepository;
    }

    public void setCurrentSongWithId(int currentSongId) {
        currentSong = songRepository.getSongById(currentSongId);
    }

    public SongItem getCurrentSong(){
        return currentSong;
    }

    public boolean hasNext(){
            return currentSong.getId() + 1 < songRepository.count();
    }

    public boolean hasNextOnPlayOrder(){
        return currentSong.getPlayOrder() + 1 < songRepository.count();
    }

    public boolean hasPrev(){
        return currentSong.getId() != 0;
    }

    public boolean hasPrevOnPlayOrder(){
        return currentSong.getPlayOrder() != 0;
    }

    public void saveLastPlayedState(long timePlayed, boolean getShuffled, int getRepeat){
        lastPlayedStateItem = new LastPlayedStateItem(
                currentSong.getId(), currentSong.getSongId(),
                timePlayed, getShuffled, getRepeat);

        songRepository.saveLastPlayedState(lastPlayedStateItem);
    }

    public void loadLastPlayedState() {
        lastPlayedStateItem = songRepository.getLastPlayedState();
    }

    public LastPlayedStateItem getLastPlayedState() {
        return lastPlayedStateItem;
    }

    public void setCurrentSong(SongItem song) {
        currentSong = song;
    }
}
