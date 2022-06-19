package com.kelompok06_RPL.mediapad;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MscAdapter extends RecyclerView.Adapter<MscAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MusicFiles> mFiles;
    BottomSheetDialog bottomSheetDialog;

    public MscAdapter(Context context, ArrayList<MusicFiles> mFiles) {
        this.context = context;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(mFiles.get(position).getTitle());
        byte[] image = getAlbum(mFiles.get(position).getPath());
        if (image != null) {
            Glide.with(context).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_baseline_library_music_24)
                    .into(holder.album_art);
        }
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(context,R.style.BottomSheetTheme);
                View bsView = LayoutInflater.from(context).inflate(R.layout.music_bs_layout,
                        v.findViewById(R.id.bs_msc));
                bsView.findViewById(R.id.bs_play_msc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.itemView.performClick();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bsView);
                bottomSheetDialog.show();
                bsView.findViewById(R.id.bs_delete_msc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Delete");
                        alertDialog.setMessage("Do you want to delete this Music");
                        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFile(position, v);
                            }
                        });
                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        bottomSheetDialog.dismiss();
                    }
                });
                bsView.findViewById(R.id.bs_add_msc).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Add playlist");
                        alertDialog.setMessage("Do you want add to playlist!");
                        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MscPlayerAcitvity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    private void deleteFile(int position, View v) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , Long.parseLong(mFiles.get(position).getId()));
        File file = new File(mFiles.get(position).getPath());
        boolean deleted = file.delete();
        if (deleted) {
            context.getContentResolver().delete(contentUri, null, null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());
            Snackbar.make(v, "File Deleted : ", Snackbar.LENGTH_LONG)
                    .show();
        } else {
            Snackbar.make(v, "Can't be Deleted : ", Snackbar.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView album_art, menu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.msc_mg);
            menu = itemView.findViewById(R.id.msc_menu_set);
        }
    }


    private byte[] getAlbum(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    void updateList(ArrayList<MusicFiles> musicFilesArrayList) {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
