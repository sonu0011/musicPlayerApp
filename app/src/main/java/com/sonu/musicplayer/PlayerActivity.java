package com.sonu.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sonu.musicplayer.activity.PlaylistSongsActivity;

import java.util.ArrayList;

import static com.sonu.musicplayer.AlbumsDetailsActivity.albumSongs;

public class PlayerActivity extends AppCompatActivity {
    TextView song_name, artist_name, duration_played, duration_total;
    ImageView covert_art, next_btn, prev_btn, shuffle_btn, repeat_btn, imageViewGradient;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int position = -1;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playPauseThread, nextThread, prevThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
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
        updateUi();


    }

    private void updateUi() {
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }

        });
    }

    private String formattedTime(int mCurrentPosition) {

        String totalOut = "", totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":0" + seconds;

        if (seconds.length() == 1) {
            return totalNew;
        } else
            return totalOut;

    }

    @Override
    protected void onResume() {
        super.onResume();
        playThreadMethod();
        nextThreadMethod();
        prevThreadMethod();
    }

    private void prevThreadMethod() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prev_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();

    }

    private void prevBtnClicked() {
        if (mediaPlayer.isPlaying()) {

            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position - 1) < 0 ? (musicFiles.size() - 1) : (position - 1));
            uri = Uri.parse(musicFiles.get(position).getPath());
            mediaPlayer = MediaPlayer.create(this, uri);
            metaData(uri);
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            updateUi();
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();

        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position - 1) < 0 ? (musicFiles.size() - 1) : (position - 1));
            uri = Uri.parse(musicFiles.get(position).getPath());
            mediaPlayer = MediaPlayer.create(this, uri);
            metaData(uri);
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            updateUi();
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    private void playThreadMethod() {
        playPauseThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playPauseThread.start();
    }

    private void playPauseBtnClicked() {

        if (mediaPlayer.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            updateUi();
        } else {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            updateUi();
        }
    }

    private void nextThreadMethod() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();

    }

    private void nextBtnClicked() {

        if (mediaPlayer.isPlaying()) {

            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position + 1) % musicFiles.size());
            uri = Uri.parse(musicFiles.get(position).getPath());
            mediaPlayer = MediaPlayer.create(this, uri);
            metaData(uri);
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            updateUi();
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();

        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position + 1) % musicFiles.size());
            uri = Uri.parse(musicFiles.get(position).getPath());
            mediaPlayer = MediaPlayer.create(this, uri);
            metaData(uri);
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            updateUi();
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");
        if (sender != null && sender.equals("album")) {
            musicFiles = albumSongs;
        } else if (sender != null && sender.equals("playlist")) {
            musicFiles = PlaylistSongsActivity.playlistSongs;
        } else {
            musicFiles = MainActivity.musicFiles;
        }
        if (musicFiles != null) {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            uri = Uri.parse(musicFiles.get(position).getPath());
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

        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);

    }

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration_total);
        covert_art = findViewById(R.id.cover_art);
        next_btn = findViewById(R.id.next);
        prev_btn = findViewById(R.id.previous);
        shuffle_btn = findViewById(R.id.shuffle);
        repeat_btn = findViewById(R.id.repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekbar);
        imageViewGradient = findViewById(R.id.imageViewGradient);

    }

    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(musicFiles.get(position).getDuration());
        duration_total.setText(formattedTime(durationTotal / 1000));
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null) {
            Glide.with(this)
                    .load(art)
                    .into(covert_art);
            imageViewGradient.setVisibility(View.GONE);
        }

        song_name.setText(musicFiles.get(position).getTitle());
        artist_name.setText(musicFiles.get(position).getArtist());
    }
}