package com.sonu.musicplayer.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sonu.musicplayer.AlbumsDetailsActivity;
import com.sonu.musicplayer.R;
import com.sonu.musicplayer.activity.PlaylistSongsActivity;
import com.sonu.musicplayer.model.Playlist;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private ArrayList<Playlist> playlists;

    public PlaylistAdapter(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.playlist_item,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaylistAdapter.ViewHolder holder, final int position) {
        holder.playlistName.setText(playlists.get(position).getPlayListName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), PlaylistSongsActivity.class)
                        .putExtra("playListId", playlists.get(position).getPlayListId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlist_name);
        }
    }
}
