package com.kelompok06_RPL.mediapad;

import static com.kelompok06_RPL.mediapad.AlbumDetailsAdapter.aFiles;
import static com.kelompok06_RPL.mediapad.MscActivity.musicFiles;
import static com.kelompok06_RPL.mediapad.MscActivity.repeatBoolean;
import static com.kelompok06_RPL.mediapad.MscActivity.shuffelBoolean;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MscPlayerAcitvity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name, artist_name, duration, duration_total;
    ImageView cover_art, next, prev, shuffle, repeatBtn;
    FloatingActionButton play;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<MusicFiles> listSong = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msc_player_acitvity);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_primary_variant)));

        initViews();
        getIntentMethod();
        song_name.setText(listSong.get(position).getTitle());
        artist_name.setText(listSong.get(position).getArtist());
        getSupportActionBar().setTitle("Now Playing");
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        MscPlayerAcitvity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffelBoolean) {
                    shuffelBoolean = false;
                    shuffle.setImageResource(R.drawable.ic_round_shuffle_24);
                } else {
                    shuffelBoolean = true;
                    shuffle.setImageResource(R.drawable.ic_round_shuffle_on_24);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatBoolean) {
                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_round_repeat_24);
                } else {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_round_repeat_on_24);
                }
            }
        });
    }

    private String formattedTime(int mCurrentPosition) {
        String total = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String mns = String.valueOf(mCurrentPosition / 60);
        total = mns + ":" + seconds;
        totalNew = mns + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return total;
        }
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClick();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextBtnClick() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffelBoolean && !repeatBoolean) {
                position = getRandom(listSong.size() - 1);
            } else if (!shuffelBoolean && !repeatBoolean) {
                position = ((position + 1) % listSong.size());
            }
            uri = Uri.parse(listSong.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSong.get(position).getTitle());
            artist_name.setText(listSong.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            MscPlayerAcitvity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play.setBackgroundResource(R.drawable.ic_round_pause_24);
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffelBoolean && !repeatBoolean) {
                position = getRandom(listSong.size() - 1);
            } else if (!shuffelBoolean && !repeatBoolean) {
                position = ((position + 1) % listSong.size());
            }
            uri = Uri.parse(listSong.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSong.get(position).getTitle());
            artist_name.setText(listSong.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            MscPlayerAcitvity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play.setBackgroundResource(R.drawable.ic_round_play_arrow_24);

        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClick();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevBtnClick() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffelBoolean && !repeatBoolean) {
                position = getRandom(listSong.size() - 1);
            } else if (!shuffelBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSong.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSong.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSong.get(position).getTitle());
            artist_name.setText(listSong.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            MscPlayerAcitvity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play.setBackgroundResource(R.drawable.ic_round_pause_24);
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffelBoolean && !repeatBoolean) {
                position = getRandom(listSong.size() - 1);
            } else if (!shuffelBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSong.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSong.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSong.get(position).getTitle());
            artist_name.setText(listSong.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            MscPlayerAcitvity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play.setBackgroundResource(R.drawable.ic_round_play_arrow_24);

        }
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playBtnClick();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playBtnClick() {
        if (mediaPlayer.isPlaying()) {
            play.setImageResource(R.drawable.ic_round_play_arrow_24);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            MscPlayerAcitvity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            play.setImageResource(R.drawable.ic_round_pause_24);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            MscPlayerAcitvity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("Sender");
        if (sender != null && sender.equals("albumDetails")) {
            listSong = aFiles;
        } else {
            listSong = musicFiles;
        }
        if (listSong != null) {
            play.setImageResource(R.drawable.ic_round_pause_24);
            uri = Uri.parse(listSong.get(position).getPath());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        } else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        int duration = mediaPlayer.getDuration() / 1000;
        seekBar.setMax(duration);
        metaData(uri);
    }

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration = findViewById(R.id.duration_msc);
        duration_total = findViewById(R.id.total_duration_msc);
        cover_art = findViewById(R.id.cover_art);
        next = findViewById(R.id.next_mc);
        prev = findViewById(R.id.prev_mc);
        shuffle = findViewById(R.id.shuffle_off);
        repeatBtn = findViewById(R.id.repeat);
        play = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekbar_mc);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_msc, menu);
        MenuItem menuItem = menu.findItem(R.id.menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSong.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null) {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this, cover_art, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        ImageView gradient = findViewById(R.id.img_gradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    } else {
                        ImageView gradient = findViewById(R.id.img_gradient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }
            });
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.ic_baseline_library_music_24)
                    .into(cover_art);
            ImageView gradient = findViewById(R.id.img_gradient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            gradient.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }
    }

    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap) {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClick();
        if (mediaPlayer != null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}