package com.sonu.musicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SongsFragment extends Fragment {
    private RecyclerView songsRecyclerView;
    private MusicAdapter musicAdapter;

    public SongsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        songsRecyclerView = view.findViewById(R.id.songs_recyclevie);
        songsRecyclerView.setHasFixedSize(true);
        if (MainActivity.musicFiles.size() > 0) {
            musicAdapter = new MusicAdapter(MainActivity.musicFiles, 0);
            songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            songsRecyclerView.setAdapter(musicAdapter);
        }
        return view;
    }
}