package com.sonu.musicplayer.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sonu.musicplayer.model.MusicFiles;
import com.sonu.musicplayer.R;
import com.sonu.musicplayer.adapter.MusicAdapter;

import java.util.ArrayList;

import static com.sonu.musicplayer.activity.MainActivity.musicFiles;

public class AlbumsDetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String albumName;
    public static ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    int j = 0;
    private ImageView art, gradient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_details);
            albumSongs.clear();
            recyclerView = findViewById(R.id.album_details_recyclerView);
            albumName = getIntent().getStringExtra("albumName");
            art = findViewById(R.id.album_details_image);
            gradient = findViewById(R.id.album_details_gradient);
            for (int i = 0; i < musicFiles.size(); i++) {

                if (albumName.equals(musicFiles.get(i).getAlbum())) {
                    albumSongs.add(j, musicFiles.get(i));
                    j++;
                }
            }
            if (albumSongs.size() >0){
                byte[] art = getAlbumArt(albumSongs.get(0).getPath());

                if (art != null) {
                    Glide.with(this)
                            .load(art)
                            .into(this.art);
                    gradient.setVisibility(View.GONE);
                }
                MusicAdapter musicAdapter = new MusicAdapter(albumSongs , 1);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(musicAdapter);
            }
            else {
                Toast.makeText(this, "No songs Found", Toast.LENGTH_SHORT).show();
            }

        }

        private byte[] getAlbumArt(String uri) {

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            byte[] art = retriever.getEmbeddedPicture();
            retriever.release();
            return art;

        }
    }