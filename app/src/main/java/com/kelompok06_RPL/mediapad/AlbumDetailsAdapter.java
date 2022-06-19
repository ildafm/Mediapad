package com.kelompok06_RPL.mediapad;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.ViewHolder> {
    private Context mContext;
    static ArrayList<MusicFiles> aFiles;
    View view;
    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> aFiles) {
        this.mContext = mContext;
        this.aFiles = aFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(aFiles.get(position).getTitle());
        byte[] image = getAlbum(aFiles.get(position).getPath());
        if (image != null) {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.logo_depan)
                    .into(holder.album);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MscPlayerAcitvity.class);
                intent.putExtra("Sender", "albumDetails");
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return aFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView album;
        TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            album = itemView.findViewById(R.id.msc_mg);
            title = itemView.findViewById(R.id.music_file_name);
        }
    }
    private byte[] getAlbum(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
