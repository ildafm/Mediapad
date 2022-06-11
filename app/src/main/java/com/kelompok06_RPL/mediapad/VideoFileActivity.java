package com.kelompok06_RPL.mediapad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.ArrayList;

public class VideoFileActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView;
    private ArrayList<FileMedia> videoFileArrayList = new ArrayList<>();
    static VideoFIleAdapter videoFIleAdapter;
    String folder_name;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_file);
        folder_name = getIntent().getStringExtra("folderName");
        getSupportActionBar().setTitle(folder_name);
        recyclerView = findViewById(R.id.rv_video);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_vid);
        showVideoFiles();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showVideoFiles();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
       ArrayList<FileMedia> video_file = new ArrayList<>();
       Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
       String selection = MediaStore.Video.Media.DATA+" like?";
       String[] sArg = new String[]{"%"+folderName+"%"};
       Cursor cursor = getContentResolver().query(uri, null,
               selection,sArg,null);
       if (cursor!=null&&cursor.moveToNext()){
           do {
               @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
               @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
               @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
               @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
               @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
               @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
               @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));

               FileMedia fileMedia = new FileMedia(id, title, name, size, path, date, duration);
               video_file.add(fileMedia);
           }while (cursor.moveToNext());
       }
        return video_file;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_video);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String inputs = newText.toLowerCase();
        ArrayList<FileMedia> fileMedia = new ArrayList<>();
        for (FileMedia media:videoFileArrayList) {
            if (media.getTitle().toLowerCase().contains(inputs)) {
                fileMedia.add(media);
            }
        }
        VideoFileActivity.videoFIleAdapter.updateVideoFile(fileMedia);
        return true;
    }
}