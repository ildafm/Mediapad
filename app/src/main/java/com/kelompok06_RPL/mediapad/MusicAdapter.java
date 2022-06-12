package com.kelompok06_RPL.mediapad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{
    ArrayList<AudioModel> songList;
    Context context;

    public MusicAdapter(ArrayList<AudioModel> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        return new MusicAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AudioModel songData = songList.get(position);
        holder.title.setText(songData.getTitle());

        if (MyMusicPlayer.currentIndex == position) {
            holder.title.setTextColor(Color.parseColor("#FF0000"));
        } else {
            holder.title.setTextColor(Color.parseColor("#FFFFFFFF"));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMusicPlayer.getInstance().reset();
                MyMusicPlayer.currentIndex = position;
                Intent intent = new Intent(context, MusicPlayer.class);
                intent.putExtra("List", songList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.music_title);
            icon = itemView.findViewById(R.id.icon_msc);
        }
    }
}
