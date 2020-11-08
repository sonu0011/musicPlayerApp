package com.sonu.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sonu.musicplayer.adapter.MusicAdapter;
import com.sonu.musicplayer.model.MusicFiles;
import com.sonu.musicplayer.R;

import java.util.ArrayList;

public class PlaylistSongsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String playListId;
    public static ArrayList<MusicFiles> playlistSongs = new ArrayList<>();
    int j = 0;
    private ImageView art, gradient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);
        playlistSongs.clear();
        initViews();
        playListId = getIntent().getStringExtra("playListId");

        playlistSongs = getAllSongs(playListId, this);

        byte[] art = getAlbumArt(playlistSongs.get(0).getPath());

        if (art != null) {
            Glide.with(this)
                    .load(art)
                    .into(this.art);
//            gradient.setVisibility(View.GONE);
        }
        MusicAdapter musicAdapter = new MusicAdapter(playlistSongs , 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(musicAdapter);


    }

    private void initViews() {
        recyclerView = findViewById(R.id.playlist_songs_recyclerView);
        art = findViewById(R.id.playlist_details_image);
//        gradient = findViewById(R.id.playlist_details_gradient);

    }

    private byte[] getAlbumArt(String uri) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;

    }

    private ArrayList<MusicFiles> getAllSongs(String playListId, Context context) {
        ArrayList<MusicFiles> list = new ArrayList<>();
        Uri membersUri = MediaStore.Audio.Playlists.Members.getContentUri("external", Long.parseLong(playListId));

        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
        };


        Cursor cursor = context.getContentResolver().query(membersUri, projection, null,
                null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration);
                list.add(musicFiles);
            }
            cursor.close();
        }
        return list;
    }


}