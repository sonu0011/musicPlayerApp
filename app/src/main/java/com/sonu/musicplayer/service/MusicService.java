package com.sonu.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sonu.musicplayer.model.MusicFiles;
import com.sonu.musicplayer.interfaces.ActionPlaying;

import java.util.ArrayList;

import static com.sonu.musicplayer.activity.PlayerActivity.allsongs;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder iBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> songs = new ArrayList<>();
    private Uri uri;
    private int position = -1;


    ActionPlaying actionPlaying;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("MusicService", "bind");
        return iBinder;
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int mPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");
        if (mPosition != -1) {
            playMedia(mPosition);
        }
        if (actionName != null) {

            switch (actionName) {
                case "playPause":
                    if (actionPlaying != null) {
                        actionPlaying.playPauseBtnClicked();
                    }
                    break;

                case "next":
                    if (actionPlaying != null) {
                        actionPlaying.nextBtnClicked();
                    }
                    break;

                case "previous":
                    if (actionPlaying != null) {
                        actionPlaying.prevBtnClicked();
                    }
                    break;
            }
        }

        return START_STICKY;
    }

    private void playMedia(int startPosition) {
        songs = allsongs;
        position = startPosition;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (songs != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        } else {
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    public void start() {
        mediaPlayer.start();
    }

    public void release() {
        mediaPlayer.release();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public void createMediaPlayer(int position) {
        uri = Uri.parse(songs.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void onCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (actionPlaying != null) {
            actionPlaying.nextBtnClicked();
        }
        createMediaPlayer(position);
        mediaPlayer.start();
        onCompleted();
    }

    public void setCallBack(ActionPlaying playing) {
        this.actionPlaying = playing;
    }

}
