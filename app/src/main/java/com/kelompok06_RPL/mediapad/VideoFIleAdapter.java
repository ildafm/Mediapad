package com.kelompok06_RPL.mediapad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class VideoFIleAdapter extends RecyclerView.Adapter<VideoFIleAdapter.ViewHolder> {
    private ArrayList<FileMedia> videoList;
    private Context context;

    public VideoFIleAdapter(ArrayList<FileMedia> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.vidName.setText(videoList.get(position).getDisplayName());
        String size = videoList.get(position).getSize();
        holder.vidSize.setText(android.text.format.Formatter.formatFileSize(context,
                Long.parseLong(size)));
        double millisecond = Double.parseDouble(videoList.get(position).getDuration());
        holder.vidDur.setText(timeConversion((long) millisecond));
//        holder.vidDur.setText((CharSequence) new File(videoList.get(position).getDuration()));
        Glide.with(context)
                .load(new File(videoList.get(position).getPath()))
                .into(holder.thumbnail);

//        Glide.with(context)
//                .load(Uri.fromFile(new File(videoList.get(position).getPath())))
//                .thumbnail(0.1f)
//                .into(holder.thumbnail);
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "menu more", Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("video_title" , videoList.get(position).getDisplayName());
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("videoArrayList", videoList);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, menu;
        TextView vidName, vidSize, vidDur;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.iv_thumb);
            menu = itemView.findViewById(R.id.vid_menu_more);
            vidName = itemView.findViewById(R.id.vid_name);
            vidSize = itemView.findViewById(R.id.video_size);
            vidDur = itemView.findViewById(R.id.tv_duration);
        }
    }

    public String timeConversion(long value) {
        String videoTime;
        int duration = (int) value;
        int hrs = (duration / 3600000);
        int mns = (duration / 60000) % 60000;
        int scs = duration % 60000 / 1000;
        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;
    }
}
