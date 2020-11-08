package com.sonu.musicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public class AlbumsFragment extends Fragment {
    private RecyclerView albumRecyclerView;
    private AlbumAdapter albumAdapter;
    public AlbumsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_albums, container, false);
        albumRecyclerView = view.findViewById(R.id.album_recyclerView);
        albumRecyclerView.setHasFixedSize(true);
        if (MainActivity.albums.size() > 0) {
            albumAdapter = new AlbumAdapter(MainActivity.albums);
            albumRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            albumRecyclerView.setAdapter(albumAdapter);
        }
        return view;
    }
}