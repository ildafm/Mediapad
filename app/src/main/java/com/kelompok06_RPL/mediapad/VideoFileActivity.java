package com.kelompok06_RPL.mediapad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.ArrayList;

public class VideoFileActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public static final String MY_PREF = "my pref";
    RecyclerView recyclerView;
    private ArrayList<FileMedia> videoFileArrayList = new ArrayList<>();
    static VideoFIleAdapter videoFIleAdapter;
    String folder_name;
    SwipeRefreshLayout swipeRefreshLayout;
    String sort_Order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_file);
        folder_name = getIntent().getStringExtra("folderName");
        getSupportActionBar().setTitle(folder_name);
        recyclerView = findViewById(R.id.rv_video);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_vid);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
        editor.putString("playlistFolderName", folder_name);
        editor.apply();

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
        videoFIleAdapter = new VideoFIleAdapter(videoFileArrayList, this,0);
        recyclerView.setAdapter(videoFIleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));
        videoFIleAdapter.notifyDataSetChanged();
    }

    private ArrayList<FileMedia> fetchMedia(String folderName) {
        SharedPreferences preferences = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        String sort_value = preferences.getString("sort", "xyz");
        ArrayList<FileMedia> video_file = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        if (sort_value.equals("sortName")) {
            sort_Order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
        } else if (sort_value.equals("sortSize")) {
            sort_Order = MediaStore.MediaColumns.SIZE + " DESC";
        } else if (sort_value.equals("sortDate")) {
            sort_Order = MediaStore.MediaColumns.DATE_ADDED + " DESC";
        } else {
            sort_Order = MediaStore.Video.Media.DURATION + " DESC";
        }

        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] sArg = new String[]{"%" + folderName + "%"};
        Cursor cursor = getContentResolver().query(uri, null,
                selection, sArg, sort_Order);
        if (cursor != null && cursor.moveToNext()) {
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
            } while (cursor.moveToNext());
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences preferences = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int id = item.getItemId();
        switch (id) {
            case R.id.refresh_file:
                finish();
                startActivity(getIntent());
                break;
            case R.id.sort_by:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Sort By");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.apply();
                        finish();
                        startActivity(getIntent());
                        dialog.dismiss();
                    }
                });
                String[] items = {"Name (A to Z)", "Size (Big to Small)", "Date (New to Old)",
                        "Length (Long to Short)"};
                alertDialog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                editor.putString("sort", "sortName");
                                break;
                            case 1:
                                editor.putString("sort", "sortSize");
                                break;
                            case 2:
                                editor.putString("sort", "sortDate");
                                break;
                            case 3:
                                editor.putString("sort", "sortLength");
                                break;
                        }
                    }
                });
                alertDialog.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String inputs = newText.toLowerCase();
        ArrayList<FileMedia> fileMedia = new ArrayList<>();
        for (FileMedia media : videoFileArrayList) {
            if (media.getTitle().toLowerCase().contains(inputs)) {
                fileMedia.add(media);
            }
        }
        VideoFileActivity.videoFIleAdapter.updateVideoFile(fileMedia);
        return true;
    }
}