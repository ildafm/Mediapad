package com.kelompok06_RPL.mediapad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private ArrayList<FileMedia> fileMedia;
    private ArrayList<String> folderPath;
    private Context context;

    public FolderAdapter(ArrayList<FileMedia> fileMedia, ArrayList<String> folderPath, Context context) {
        this.fileMedia = fileMedia;
        this.folderPath = folderPath;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.folder_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        /storage/media/video
        int indexPath = folderPath.get(position).lastIndexOf("/");
        String name_folder = folderPath.get(position).substring(indexPath+1);
        holder.folder_Name.setText(name_folder);
        holder.folder_Path.setText(folderPath.get(position));
        holder.noOfFiles.setText(noOfFolder(folderPath.get(position))+ " Video");
        Glide.with(context)
                .load(new File(fileMedia.get(position).getPath()))
                .into(holder.thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoFileActivity.class);
                intent.putExtra("folderName", name_folder);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return folderPath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView folder_Name, folder_Path, noOfFiles;
        ImageView thumbnail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folder_Name = itemView.findViewById(R.id.folder_name);
            folder_Path = itemView.findViewById(R.id.folder_path);
            noOfFiles = itemView.findViewById(R.id.noOfFiles);
            thumbnail = itemView.findViewById(R.id.icon_folder);
        }
    }

    int noOfFolder(String folder_name) {
        int files_no = 0;
        for (FileMedia fileMedia : fileMedia) {
            if (fileMedia.getPath().substring(0,fileMedia.getPath().lastIndexOf("/"))
                    .endsWith(folder_name)) {
                files_no++;
            }
        }
        return files_no;
    }
}
