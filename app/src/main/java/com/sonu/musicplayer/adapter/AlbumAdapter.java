package com.sonu.musicplayer;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private ArrayList<MusicFiles> albumsList;

    public AlbumAdapter(ArrayList<MusicFiles> albumsList) {
        this.albumsList = albumsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.album_name.setText(albumsList.get(position).getAlbum());
        byte[] art = getAlbumArt(albumsList.get(position).getPath());

        if (art != null) {
            Glide.with(holder.album_image.getContext())
                    .load(art)
                    .into(holder.album_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), AlbumsDetailsActivity.class)
                        .putExtra("albumName", albumsList.get(position).getAlbum()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView album_name;
        ImageView album_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.album_image);
            album_name = itemView.findViewById(R.id.album_name);
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
