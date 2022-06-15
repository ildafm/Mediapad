package com.kelompok06_RPL.mediapad;

import android.annotation.SuppressLint;
import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;

public class VidPlayer extends AppCompatActivity  implements View.OnClickListener {

    ArrayList<FileMedia> mVideoFile = new ArrayList<>();
    PlayerView playerView;
    SimpleExoPlayer player;
    int position;
    String videoTitle;
    TextView title;
    private VidPlayer.ControlsMode controlsMode;

    public enum ControlsMode {
        LOCK, FULLSCREEN;
    }
    ConcatenatingMediaSource concatenatingMediaSource;
    ImageView next, prev, video_back, lock, unlock, scalling, vid_list;
    VideoFIleAdapter videoFIleAdapter;
    RelativeLayout root;
    // Horizontal rcv variables
    private ArrayList<IconModel> iconModelsArrayList = new ArrayList<>();
    PlaybackIconAdapter playbackIconAdapter;
    RecyclerView recyclerViewIcon;
    boolean expand = false;
    View night_mode;
    boolean dark = false;
    boolean mute = false;
    PlaybackParameters parameters;
    float speed;
    PictureInPictureParams.Builder pictureInPicture;
    boolean isCrossChecked;
    DialogProperties properties = new DialogProperties();
    FilePickerDialog filePickerDialog;
    Uri uriSubtitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_vid_player);
        getSupportActionBar().hide();
        playerView = findViewById(R.id.exo_player_view);
        position = getIntent().getIntExtra("position", 1);
        videoTitle = getIntent().getStringExtra("video_title");
        mVideoFile = getIntent().getExtras().getParcelableArrayList("videoArrayList");
        screenOrientation();

        next = findViewById(R.id.exo_next);
        prev = findViewById(R.id.exo_prev);
        title = findViewById(R.id.vid_title);
        video_back = findViewById(R.id.vid_back);
        lock = findViewById(R.id.lock);
        unlock = findViewById(R.id.unlock);
        scalling = findViewById(R.id.scalling);

        filePickerDialog = new FilePickerDialog(VidPlayer.this);
        filePickerDialog.setTitle("Select a Subtitle file");
        filePickerDialog.setPositiveBtnName("OK");
        filePickerDialog.setNegativeBtnName("Cancel");

        root = findViewById(R.id.root_layout);
        night_mode = findViewById(R.id.night_more);
        vid_list = findViewById(R.id.vid_list);
        title.setText(videoTitle);
        recyclerViewIcon = findViewById(R.id.rcv_icon);

        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        video_back.setOnClickListener(this);
        lock.setOnClickListener(this);
        unlock.setOnClickListener(this);
        vid_list.setOnClickListener(this);
        scalling.setOnClickListener(firstListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pictureInPicture = new PictureInPictureParams.Builder();
        }

        iconModelsArrayList.add(new IconModel(R.drawable.ic_right, ""));
        iconModelsArrayList.add(new IconModel(R.drawable.ic_night_mode, "Night"));
        iconModelsArrayList.add(new IconModel(R.drawable.ic_pip_mode, "Popup"));
        iconModelsArrayList.add(new IconModel(R.drawable.ic_equal, "Equalizer"));
        iconModelsArrayList.add(new IconModel(R.drawable.ic_rotate, "Rotate"));

        playbackIconAdapter = new PlaybackIconAdapter(iconModelsArrayList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                RecyclerView.HORIZONTAL, true);
        recyclerViewIcon.setLayoutManager(layoutManager);
        recyclerViewIcon.setAdapter(playbackIconAdapter);
        playbackIconAdapter.notifyDataSetChanged();
        playbackIconAdapter.setOnItemClickListener(new PlaybackIconAdapter.OnItemClickListener() {
            @SuppressLint("Range")
            @Override
            public void onItemClick(int position) {
                if (position == 0) {
                    if (expand) {
                        iconModelsArrayList.clear();
                        iconModelsArrayList.add(new IconModel(R.drawable.ic_right, ""));
                        iconModelsArrayList.add(new IconModel(R.drawable.ic_night_mode, "Night"));
                        iconModelsArrayList.add(new IconModel(R.drawable.ic_pip_mode, "Popup"));
                        iconModelsArrayList.add(new IconModel(R.drawable.ic_equal, "Equalizer"));
                        iconModelsArrayList.add(new IconModel(R.drawable.ic_rotate, "Rotate"));
                        playbackIconAdapter.notifyDataSetChanged();
                        expand = false;
                    } else {
                        if (iconModelsArrayList.size() == 5) {
                            iconModelsArrayList.add(new IconModel(R.drawable.ic_volume, "Mute"));
                            iconModelsArrayList.add(new IconModel(R.drawable.ic_volume1, "Volume"));
                            iconModelsArrayList.add(new IconModel(R.drawable.ic_bright, "Brightness"));
                            iconModelsArrayList.add(new IconModel(R.drawable.ic_speed, "Speed"));
                            iconModelsArrayList.add(new IconModel(R.drawable.ic_subtitles, "Subtitle"));
                        }
                        iconModelsArrayList.set(position, new IconModel(R.drawable.ic_left, ""));
                        playbackIconAdapter.notifyDataSetChanged();
                        expand = true;
                    }
                }
                if (position == 1) {
                    // Night Mode
                    if (dark) {
                        night_mode.setVisibility(View.GONE);
                        iconModelsArrayList.set(position, new IconModel(R.drawable.ic_night_mode, "Night"));
                        playbackIconAdapter.notifyDataSetChanged();
                        dark = false;
                    } else {
                        night_mode.setVisibility(View.VISIBLE);
                        iconModelsArrayList.set(position, new IconModel(R.drawable.ic_night_mode, "Day"));
                        playbackIconAdapter.notifyDataSetChanged();
                        dark = true;
                    }
                }
                if (position == 2) {
                    // Popup
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Rational aspectRatio = new Rational(16, 9);
                        pictureInPicture.setAspectRatio(aspectRatio);
                        enterPictureInPictureMode(pictureInPicture.build());
                    } else {
                        Log.wtf("not oreo", "yes");
                    }
                }
                if (position == 3) {
                    // Equalizer
                    Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                    if ((intent.resolveActivity(getPackageManager()) != null)) {
                        startActivityForResult(intent, 123);
                    } else {
                        Toast.makeText(VidPlayer.this, "No Equalizer Fond", Toast.LENGTH_SHORT).show();
                    }
                    playbackIconAdapter.notifyDataSetChanged();


                }
                if (position == 4) {
                    // Routet
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        playbackIconAdapter.notifyDataSetChanged();
                    } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        playbackIconAdapter.notifyDataSetChanged();
                    }


                }
                if (position == 5) {
                    // Mute
                    if (mute) {
                        player.setVolume(100);
                        iconModelsArrayList.set(position, new IconModel(R.drawable.ic_volume, "Mute"));
                        playbackIconAdapter.notifyDataSetChanged();
                        mute = false;
                    } else {
                        player.setVolume(0);
                        iconModelsArrayList.set(position, new IconModel(R.drawable.ic_volume1, "Unmute"));
                        playbackIconAdapter.notifyDataSetChanged();
                        mute = true;
                    }

                }
                if (position == 6) {
                    // Volume
                    VolumeDialog volumeDialog = new VolumeDialog();
                    volumeDialog.show(getSupportFragmentManager(), "dialog");
                    playbackIconAdapter.notifyDataSetChanged();
                }
                if (position == 7) {
                    // Brightness
                    BrightnessDialog brightnessDialog = new BrightnessDialog();
                    brightnessDialog.show(getSupportFragmentManager(), "dialog");
                    playbackIconAdapter.notifyDataSetChanged();


                }
                if (position == 8) {
                    // Speed
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(VidPlayer.this);
                    alertDialog.setTitle("Select Playback Speed").setPositiveButton("OK", null);
                    String[] items = {"0.5x", "1x", "1.25x", "1.5x", "2.0x"};
                    int checkItem = -1;
                    alertDialog.setSingleChoiceItems(items, checkItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    speed = 0.5f;
                                    parameters = new PlaybackParameters(speed);
                                    player.setPlaybackParameters(parameters);
                                    break;
                                case 1:
                                    speed = 1f;
                                    parameters = new PlaybackParameters(speed);
                                    player.setPlaybackParameters(parameters);
                                    break;
                                case 2:
                                    speed = 1.25f;
                                    parameters = new PlaybackParameters(speed);
                                    player.setPlaybackParameters(parameters);
                                    break;
                                case 3:
                                    speed = 1.5f;
                                    parameters = new PlaybackParameters(speed);
                                    player.setPlaybackParameters(parameters);
                                    break;
                                case 4:
                                    speed = 2f;
                                    parameters = new PlaybackParameters(speed);
                                    player.setPlaybackParameters(parameters);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                }
                if (position == 9) {
                    // Subtitle
                    properties.selection_mode = DialogConfigs.SINGLE_MODE;
                    properties.extensions = new String[]{".srt"};
                    properties.root = new File("/storage/emulated/0");
                    filePickerDialog.setProperties(properties);
                    filePickerDialog.show();
                    filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            for (String path : files ) {
                                File file = new File(path);
                                uriSubtitles = Uri.parse(file.getAbsolutePath().toString());

                            }
                            playVideoSubtitle(uriSubtitles);
                        }
                    });
                }

            }
        });

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
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(String.valueOf(uri))));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.setPlaybackParameters(parameters);
        player.prepare(concatenatingMediaSource);
        player.seekTo(position, C.TIME_UNSET);
        playError();
    }
    private void playVideoSubtitle(Uri subtitle) {
        long oldPosition = player.getCurrentPosition();
        player.stop();

        String path = mVideoFile.get(position).getPath();
        Uri uri = Uri.parse(path);
        player = new SimpleExoPlayer.Builder(this).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "Mediapad"));
        concatenatingMediaSource = new ConcatenatingMediaSource();
        for (int i = 0; i < mVideoFile.size(); i++) {
            new File(String.valueOf(mVideoFile.get(i)));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(String.valueOf(uri))));
            Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, Format.NO_VALUE, "app");
            MediaSource subtitleSource = new SingleSampleMediaSource.Factory(dataSourceFactory).setTreatLoadErrorsAsEndOfStream(true)
                    .createMediaSource(Uri.parse(String.valueOf(subtitle)), textFormat, C.TIME_UNSET);
            MergingMediaSource mergingMediaSource = new MergingMediaSource(mediaSource, subtitleSource);
            concatenatingMediaSource.addMediaSource(mergingMediaSource);
        }
        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.setPlaybackParameters(parameters);
        player.prepare(concatenatingMediaSource);
        player.seekTo(position, oldPosition);
        playError();
    }

    private void screenOrientation() {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Bitmap bitmap;
            String path = mVideoFile.get(position).getPath();
            Uri uri = Uri.parse(path);
            retriever.setDataSource(this, uri);
            bitmap = retriever.getFrameAtTime();

            int videoW = bitmap.getWidth();
            int videoH = bitmap.getHeight();
            if (videoW > videoH) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } catch (Exception e) {
            Log.e("MediaMetaDataRetriever", "screenOrientation: ");
        }
    }

    private void playError() {
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(VidPlayer.this, "Video Playing Error", Toast.LENGTH_SHORT).show();
                Player.EventListener.super.onPlayerError(error);
            }
        });
//        player.addListener(new Player.EventListener() {
//            @Override
//            public void onPlayerError(ExoPlaybackException error) {
//                Toast.makeText(VideoPlayerActivity.this, "Video Playing Error", Toast.LENGTH_SHORT).show();
//                Player.EventListener.super.onPlayerError(error);
//            }
//        });
//        player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (player.isPlaying()) {
            player.stop();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        player.getPlaybackState();
        if (isInPictureInPictureMode()) {
            player.setPlayWhenReady(true);
        } else {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vid_back:
                if (player != null) {
                    player.release();
                }
                finish();
                break;
            case R.id.vid_list:
                PlaylistDialog playlistDialog = new PlaylistDialog(mVideoFile, videoFIleAdapter);
                playlistDialog.show(getSupportFragmentManager(), playlistDialog.getTag());
                break;
            case R.id.lock:
                controlsMode = VidPlayer.ControlsMode.FULLSCREEN;
                root.setVisibility(View.VISIBLE);
                lock.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Unlock", Toast.LENGTH_SHORT).show();
                break;
            case R.id.unlock:
                controlsMode = VidPlayer.ControlsMode.LOCK;
                root.setVisibility(View.INVISIBLE);
                lock.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Locked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.exo_next:
                try {
                    player.stop();
                    position++;
                    playVideo();
                } catch (Exception e) {
                    Toast.makeText(this, "No Next Video", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.exo_prev:
                try {
                    player.stop();
                    position--;
                    playVideo();
                } catch (Exception e) {
                    Toast.makeText(this, "No Previous Video", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    View.OnClickListener firstListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            scalling.setImageResource(R.drawable.full);

            Toast.makeText(VidPlayer.this, "Full Screen", Toast.LENGTH_SHORT).show();
            scalling.setOnClickListener(secondListener);
        }
    };
    View.OnClickListener secondListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scalling.setImageResource(R.drawable.zoom);

            Toast.makeText(VidPlayer.this, "Zoom", Toast.LENGTH_SHORT).show();
            scalling.setOnClickListener(thirdListener);
        }
    };
    View.OnClickListener thirdListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scalling.setImageResource(R.drawable.exit);

            Toast.makeText(VidPlayer.this, "Fit", Toast.LENGTH_SHORT).show();
            scalling.setOnClickListener(firstListener);
        }
    };

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        isCrossChecked = isInPictureInPictureMode;
        if (isInPictureInPictureMode) {
            playerView.hideController();
        } else {
            playerView.showController();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isCrossChecked) {
            player.release();
            finish();
        }
    }
}