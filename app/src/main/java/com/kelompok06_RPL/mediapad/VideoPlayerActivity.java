package com.kelompok06_RPL.mediapad;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity {

    ArrayList<FileMedia> mVideoFile = new ArrayList<>();
    PlayerView playerView;
    SimpleExoPlayer player;
    int position;
    String videoTitle;
    TextView title;
    ConcatenatingMediaSource concatenatingMediaSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        playerView = findViewById(R.id.exo_player_view);
        getSupportActionBar().hide();
        position = getIntent().getIntExtra("position", 1);
        videoTitle = getIntent().getStringExtra("video_title");
        mVideoFile = getIntent().getExtras().getParcelableArrayList("videoArrayList");

        title = findViewById(R.id.vid_title);
        title.setText(videoTitle);

        playVideo();
    }

    private void playVideo() {
        String path = mVideoFile.get(position).getPath();
        Uri uri = Uri.parse(path);
        player = new SimpleExoPlayer.Builder(this).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "Mediapad"));
        concatenatingMediaSource = new ConcatenatingMediaSource();
        for (int i = 0; i < mVideoFile.size(); i++) {
            new File(String.valueOf(mVideoFile.get(i)));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(String.valueOf(uri)));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.prepare(concatenatingMediaSource);
        player.seekTo(position, C.TIME_UNSET);
        playError();
    }

    private void playError() {
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(VideoPlayerActivity.this, "Video Playing Error", Toast.LENGTH_SHORT).show();
                Player.EventListener.super.onPlayerError(error);
            }
        });
        player.setPlayWhenReady(true);
    }
}