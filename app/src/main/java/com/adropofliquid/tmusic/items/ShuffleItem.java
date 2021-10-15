package com.adropofliquid.tmusic.items;


import androidx.room.ColumnInfo;

public class ShuffleItem {

    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "playOrder")
    private int playOrder;

    public ShuffleItem(int id, int playOrder ) {
        this.id = id;
        this.playOrder = playOrder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayOrder() {
        return playOrder;
    }

    public void setPlayOrder(int playOrder) {
        this.playOrder = playOrder;
    }
}
