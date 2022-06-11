package com.kelompok06_RPL.mediapad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;

public class VideoFIleAdapter extends RecyclerView.Adapter<VideoFIleAdapter.ViewHolder> {
    private ArrayList<FileMedia> videoList;
    private Context context;
    BottomSheetDialog bottomSheetDialog;

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
                bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
                View bsView = LayoutInflater.from(context).inflate(R.layout.video_bs_layout,
                        view.findViewById(R.id.bottom_sheet));
                bsView.findViewById(R.id.bs_play).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.itemView.performClick();
                        bottomSheetDialog.dismiss();
                    }
                });
                bsView.findViewById(R.id.bs_rename).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Rename to");
                        EditText editText = new EditText(context);
                        String path = videoList.get(position).getPath();
                        final File file = new File(path);
                        String videoName = file.getName();
                        videoName = videoName.substring(0, videoName.lastIndexOf("."));
                        editText.setText(videoName);
                        alertDialog.setView(editText);
                        editText.requestFocus();
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String onlyPath = file.getParentFile().getAbsolutePath();
                                String ext = file.getAbsolutePath();
                                ext = ext.substring(ext.lastIndexOf("."));
                                String newPath = onlyPath + "/" + editText.getText().toString() + ext;
                                File newFile = new File(newPath);
                                boolean rename = file.renameTo(newFile);
                                if (rename) {
                                    ContentResolver resolver = context.getApplicationContext().getContentResolver();
                                    resolver.delete(MediaStore.Files.getContentUri("external"),
                                            MediaStore.MediaColumns.DATA + "=?", new String[]{file.getAbsolutePath()});
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    intent.setData(Uri.fromFile(newFile));
                                    context.getApplicationContext().sendBroadcast(intent);

                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();

                                    SystemClock.sleep(200);
                                    ((Activity) context).recreate();
                                } else {
                                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.create().show();
                        bottomSheetDialog.dismiss();
                    }
                });
                bsView.findViewById(R.id.bs_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse(videoList.get(position).getPath());
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        context.startActivity(Intent.createChooser(shareIntent, "Share video view"));
                        bottomSheetDialog.dismiss();
                    }
                });
                bsView.findViewById(R.id.bs_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        alertDialog.setTitle("Delete");
                        alertDialog.setMessage("Do you want to delete this video");
                        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri contentUri = ContentUris
                                        .withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                                Long.parseLong(videoList.get(position).getId()));
                                File file = new File(videoList.get(position).getPath());
                                boolean delete = file.delete();
                                if (delete) {
                                    context.getContentResolver().delete(contentUri, null, null);
                                    videoList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, videoList.size());
                                    Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Can't Deleted!", Toast.LENGTH_SHORT).show();
                                }
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
                bsView.findViewById(R.id.bs_properti).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                bottomSheetDialog.setContentView(bsView);
                bottomSheetDialog.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("video_title", videoList.get(position).getDisplayName());
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
