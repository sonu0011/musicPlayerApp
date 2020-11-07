package com.sonu.musicplayer.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sonu.musicplayer.AlbumAdapter;
import com.sonu.musicplayer.MainActivity;
import com.sonu.musicplayer.R;
import com.sonu.musicplayer.adapter.PlaylistAdapter;

public class PlayListFragment extends Fragment {
    private RecyclerView playlistRecyclerview;
    private PlaylistAdapter playlistAdapter;

    public PlayListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        playlistRecyclerview = view.findViewById(R.id.playlist_recyclerView);
        playlistRecyclerview.setHasFixedSize(true);
        if (MainActivity.playlists.size() > 0) {

            playlistAdapter = new PlaylistAdapter(MainActivity.playlists);
            playlistRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
            playlistRecyclerview.setAdapter(playlistAdapter);
        }
        return view;
    }
}