package com.sonu.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sonu.musicplayer.model.MusicFiles;
import com.sonu.musicplayer.R;
import com.sonu.musicplayer.interfaces.ActionPlaying;
import com.sonu.musicplayer.receivers.NotificationReceiver;
import com.sonu.musicplayer.service.MusicService;

import java.util.ArrayList;

import static com.sonu.musicplayer.activity.AlbumsDetailsActivity.albumSongs;
import static com.sonu.musicplayer.ApplicationClass.ACTION_NEXT;
import static com.sonu.musicplayer.ApplicationClass.ACTION_PLAY;
import static com.sonu.musicplayer.ApplicationClass.ACTION_PREVIOUS;
import static com.sonu.musicplayer.ApplicationClass.CHANNEL_ID_1;

public class PlayerActivity extends AppCompatActivity implements ActionPlaying,
        ServiceConnection {
    TextView song_name, artist_name, duration_played, duration_total;
    ImageView covert_art, next_btn, prev_btn, shuffle_btn, repeat_btn, imageViewGradient, btn_back;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int position = -1;
    public static ArrayList<MusicFiles> allsongs = new ArrayList<>();
    static Uri uri;
    //    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playPauseThread, nextThread, prevThread;
    private MusicService musicService;
    private MediaSessionCompat mediaSessionCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        mediaSessionCompat = new MediaSessionCompat(this, "My Audio");
        getIntentMethod();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (musicService != null && fromUser) {
                    musicService.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (musicService != null) {
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
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
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
     unbindService(this);
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

    public void prevBtnClicked() {
        if (musicService.isPlaying()) {

            musicService.stop();
            musicService.release();
            position = ((position - 1) < 0 ? (allsongs.size() - 1) : (position - 1));
            uri = Uri.parse(allsongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }

            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_baseline_pause_24);
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            musicService.start();

        } else {
            musicService.stop();
            musicService.release();
            position = ((position - 1) < 0 ? (allsongs.size() - 1) : (position - 1));
            uri = Uri.parse(allsongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }

            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_baseline_play_arrow_24);
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

    public void playPauseBtnClicked() {

        if (musicService.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            showNotification(R.drawable.ic_baseline_play_arrow_24);
            musicService.pause();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }

            });
        } else {
            showNotification(R.drawable.ic_baseline_pause_24);
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            musicService.start();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }

            });
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

    public void nextBtnClicked() {

        if (musicService.isPlaying()) {

            musicService.stop();
            musicService.release();
            position = ((position + 1) % allsongs.size());
            uri = Uri.parse(allsongs.get(position).getPath());
//            mediaPlayer = MediaPlayer.create(this, uri);
            musicService.createMediaPlayer(position);
            metaData(uri);
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }

            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_baseline_pause_24);
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            musicService.start();

        } else {
            musicService.stop();
            musicService.release();
            position = ((position + 1) % allsongs.size());
            uri = Uri.parse(allsongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                        duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }

            });
            musicService.onCompleted();
            showNotification(R.drawable.ic_baseline_play_arrow_24);
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");
        if (sender != null && sender.equals("album")) {
            allsongs = albumSongs;
        } else if (sender != null && sender.equals("playlist")) {
            allsongs = PlaylistSongsActivity.playlistSongs;
        } else {
            allsongs = MainActivity.musicFiles;
        }
        if (allsongs != null) {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            Log.e("PlayerActivity", allsongs.size() + "  position" + position);
            uri = Uri.parse(allsongs.get(position).getPath());
        }

        showNotification(R.drawable.ic_baseline_pause_24);
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);
    }

    private void initViews() {
        btn_back = findViewById(R.id.btn_back);
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
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(allsongs.get(position).getDuration());
        duration_total.setText(formattedTime(durationTotal / 1000));
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null) {
            Glide.with(this)
                    .load(art)
                    .into(covert_art);
            imageViewGradient.setVisibility(View.GONE);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_baseline_music_note_24)
                    .into(covert_art);
            imageViewGradient.setVisibility(View.VISIBLE);
        }

        song_name.setText(allsongs.get(position).getTitle());
        artist_name.setText(allsongs.get(position).getArtist());
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallBack(this);
        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);
        musicService.onCompleted();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;

    }

    void showNotification(int playPauseBtn) {

        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("notification" ,"notification");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0,
                prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0,
                pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] picture = null;

        picture = getAlbumArt(allsongs.get(position).getPath());
        Bitmap thumb = null;

        if (picture != null) {
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);

        } else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_music_note_black);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(playPauseBtn)
                .setContentTitle(allsongs.get(position).getTitle())
                .setContentText(allsongs.get(position).getArtist())
                .setLargeIcon(thumb)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }

    private byte[] getAlbumArt(String uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;

    }
}