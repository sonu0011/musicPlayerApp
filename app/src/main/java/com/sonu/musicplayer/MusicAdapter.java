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

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private ArrayList<MusicFiles> musicFiles;
    private int flag = 0;

    public MusicAdapter(ArrayList<MusicFiles> musicFiles, int i) {
        this.musicFiles = musicFiles;
        this.flag = i;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.audio_name.setText(musicFiles.get(position).getTitle());
        byte[] art = getAlbumArt(musicFiles.get(position).getPath());

        if (art != null) {
            Glide.with(holder.audio_image.getContext())
                    .load(art)
                    .into(holder.audio_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag ==1) {
                    holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), PlayerActivity.class)
                            .putExtra("position", position)
                            .putExtra("sender", "album"));

                }
                else if (flag ==2){
                    holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), PlayerActivity.class)
                            .putExtra("position", position)
                            .putExtra("sender", "playlist"));

                }
                else {
                    holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), PlayerActivity.class)
                            .putExtra("position", position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView audio_name;
        ImageView audio_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            audio_image = itemView.findViewById(R.id.audio_image);
            audio_name = itemView.findViewById(R.id.audio_name);

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
