package com.kelompok06_RPL.mediapad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;

public class VideoFileActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private ArrayList<FileMedia> videoFileArrayList = new ArrayList<>();
    VideoFIleAdapter videoFIleAdapter;
    String folder_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_file);
        folder_name = getIntent().getStringExtra("folderName");
        getSupportActionBar().setTitle(folder_name);
        recyclerView = findViewById(R.id.rv_video);

        showVideoFiles();
    }

    private void showVideoFiles() {
        videoFileArrayList = fetchMedia(folder_name);
        videoFIleAdapter = new VideoFIleAdapter(videoFileArrayList, this);
        recyclerView.setAdapter(videoFIleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));
        videoFIleAdapter.notifyDataSetChanged();
    }

    private ArrayList<FileMedia> fetchMedia(String folderName) {
        ArrayList<FileMedia> videoFile = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArg = new String[]{"%" + folderName + "%"};
        Cursor cursor = getContentResolver().query(uri, null,
                selection, selectionArg, null);
        if (cursor != null && cursor.moveToNext()) {
            do {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));

                FileMedia fileMedia = new FileMedia(id, title, name, size, duration, path, date);
                videoFile.add(fileMedia);
            } while (cursor.moveToNext());
        }
        return videoFile;
    }
}