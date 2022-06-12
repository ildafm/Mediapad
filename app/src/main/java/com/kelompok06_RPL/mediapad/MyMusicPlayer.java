package com.kelompok06_RPL.mediapad;

import android.media.MediaPlayer;

public class MyMusicPlayer {
    static MediaPlayer instance;

    public static MediaPlayer getInstance() {
        if (instance == null) {
            instance = new MediaPlayer();
        }
        return instance;
    }
    public static int currentIndex = -1;
}
