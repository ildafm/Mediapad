package com.kelompok06_RPL.mediapad;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayer extends AppCompatActivity {
    TextView title,currentTime,totalTime;
    SeekBar seekBar;
    ImageView play,next,prev,musicIcon;
    ArrayList<AudioModel> songList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMusicPlayer.getInstance();
    int x = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        title = findViewById(R.id.song_title);
        currentTime = findViewById(R.id.current_time);
        totalTime = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seekbar_msc);
        play = findViewById(R.id.play_msc);
        prev = findViewById(R.id.prev_msc);
        next = findViewById(R.id.next_msc);
        musicIcon = findViewById(R.id.music_icon_big);

        title.setSelected(true);

        songList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("List");
        setResourcesWithMusic();

        MusicPlayer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTime.setText(convertion(mediaPlayer.getCurrentPosition()+""));
                    if (mediaPlayer.isPlaying()) {
                        play.setImageResource(R.drawable.msc_pause);
                        musicIcon.setRotation(x++);
                    } else {
                        play.setImageResource(R.drawable.msc_play);
                        musicIcon.setRotation(0);

                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void setResourcesWithMusic() {
        currentSong = songList.get(MyMusicPlayer.currentIndex);

        title.setText(currentSong.getTitle());
        totalTime.setText(convertion(currentSong.getDuration()));

        play.setOnClickListener(v-> pause());
        next.setOnClickListener(v-> playNextSong());
        prev.setOnClickListener(v-> playPrevSong());

        playMusic();
    }

    private void playMusic() {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void playNextSong() {
        if (MyMusicPlayer.currentIndex == songList.size()-1)
            return;
        MyMusicPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPrevSong() {
        if (MyMusicPlayer.currentIndex == 0)
            return;
        MyMusicPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pause() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

    public static String convertion(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1) );
    }
}