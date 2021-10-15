package com.adropofliquid.tmusic.items;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class LastPlayedStateItem {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "lastSongId")
    private int lastSongId;

    @ColumnInfo(name = "timePlayed")
    private long timePlayed;

    @ColumnInfo(name = "shuffled")
    private boolean shuffled;

    @ColumnInfo(name = "repeat")
    private int repeat;

    public LastPlayedStateItem(){}

    @Ignore
    public LastPlayedStateItem(int songType) {
    }

    @Ignore
    public LastPlayedStateItem(int id, int lastSongId, long timePlayed, boolean shuffled, int repeat) {
        this.id = id;
        this.lastSongId = lastSongId;
        this.timePlayed = timePlayed;
        this.shuffled = shuffled;
        this.repeat = repeat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastSongId(int lastSongId) {
        this.lastSongId = lastSongId;
    }

    public int getLastSongId() {
        return lastSongId;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    public void setTimePlayed(long timePlayed) {
        this.timePlayed = timePlayed;
    }

    public boolean isShuffled() {
        return shuffled;
    }

    public void setShuffled(boolean shuffled) {
        this.shuffled = shuffled;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }
}
