package com.adropofliquid.tmusic.db;

import android.provider.BaseColumns;

public class FeedContract {

    private FeedContract(){}

    public static class PlayQueue implements BaseColumns {
        public static final String TABLE_NAME = "play_queue";
        public static final String COLUMN_NAME_SONG_ID = "song_id";
        public static final String COLUMN_NAME_ALBUM = "album";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_PLAY_ORDER = "play_order";
        public static final String COLUMN_NAME_PLAY_ORDER_BCK = "backup_play_order";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_URI = "uri";
        public static final String COLUMN_NAME_ALBUM_ID = "album_id";
        public static final String COLUMN_NAME_ARTIST_ID = "artist_id";
    }

    public static class LastPlayed implements BaseColumns {
        public static final String TABLE_NAME = "last_played";
        public static final String COLUMN_NAME_PLAY_POSITION = "play_position";
        public static final String COLUMN_NAME_QUEUE_POSITION = "queue_position";
    }

}
