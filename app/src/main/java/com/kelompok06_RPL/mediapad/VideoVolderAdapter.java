package com.kelompok06_RPL.mediapad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VideoVolderAdapter extends RecyclerView.Adapter<VideoVolderAdapter.ViewHolder> {
    private ArrayList<VideoFiIes> videoFiIes;
    private ArrayList<String> folderPath;
    private Context context;

    public VideoVolderAdapter(ArrayList<VideoFiIes> videoFiIes, ArrayList<String> folderPath, Context context) {
        this.videoFiIes = videoFiIes;
        this.folderPath = folderPath;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int indexPath = folderPath.get(position).lastIndexOf("/");
        String nameFolder = folderPath.get(position).substring(indexPath+1);
        holder.folderN.setText(nameFolder);
        holder.folderPath.setText(folderPath.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,VideoFragment.class);
                intent.putExtra("folderName", nameFolder);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderPath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderN, folderPath;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderN = itemView.findViewById(R.id.tv_folder);
            folderPath = itemView.findViewById(R.id.tv_folder_path);
        }
    }
}
