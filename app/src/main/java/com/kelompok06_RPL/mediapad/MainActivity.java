package com.kelompok06_RPL.mediapad;

/*
Projek UAS Rekayasa Perangkat Lunak
Kelompok 06 - Mediapad - Aplikasi Pemutar Media
Anggota :
Muhammad Fadli
Ivan Luthfi Laksono
Sthepanie
Tangguh Prana Wela Sukma
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bnvNavigationView;
    private FrameLayout flContainer;
    private ArrayList<FileMedia> fileMedia = new ArrayList<>();
    private ArrayList<String> allFolder = new ArrayList<>();
    RecyclerView recyclerView;
    FolderAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;


//    ActivityResultLauncher<String[]> PermissionResultLauncher;
//    private boolean readPermissionGanted = false;
//    private boolean locationPermissionGranted = false;
//    private boolean recordPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        PermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
//            @Override
//            public void onActivityResult(Map<String, Boolean> result) {
//                if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
//                    readPermissionGanted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                }
//            }
//
//        });

        recyclerView = findViewById(R.id.folder_rv);
        swipeRefreshLayout = findViewById(R.id.sw_refresh_folder);
        showFolder();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showFolder();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

//        requestPermission();

        getSupportActionBar().setTitle("Video");
        bnvNavigationView = findViewById(R.id.bnv_navigasi_bottom);
        bnvNavigationView.setSelectedItemId(R.id.menu_video);

        bnvNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment FR;

                switch (item.getItemId()) {
                    case R.id.menu_video:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.menu_musik:
                        startActivity(new Intent(getApplicationContext(), MusikActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.menu_playlist:
                        startActivity(new Intent(getApplicationContext(), PlayListActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.menu_browse:
                        startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }

    private void showFolder() {
        fileMedia = fethMedia();
        adapter = new FolderAdapter(fileMedia, allFolder, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter.notifyDataSetChanged();

    }

    private ArrayList<FileMedia> fethMedia() {
        ArrayList<FileMedia> fileMediaArrayList = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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
                int index = path.lastIndexOf("/");
                String subString = path.substring(0, index);
                if (!allFolder.contains(subString)) {
                    allFolder.add(subString);
                }
                fileMediaArrayList.add(fileMedia);
            } while (cursor.moveToNext());
        }
        return fileMediaArrayList;
    }


    private void bukaFragment(Fragment FRG) {
        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();
        FT.replace(R.id.fl_container, FRG);
        FT.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        getMenuInflater().inflate(R.menu.folder_menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.rateus:
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="
                        + getApplicationContext().getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.refresh_folder:
                finish();
                startActivity(getIntent());
                break;
            case R.id.share_app:
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, "Check this app via\n" +
                        "https://play.google.com/store/apps/details?id="
                        + getApplicationContext().getPackageName());
                share.setType("text/plain");
                startActivity(Intent.createChooser(share, "Share app via"));
                break;
        }
        return true;
    }

//    private void requestPermission() {
//        readPermissionGanted = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED;
//
//        List<String> permissionRequest = new ArrayList<String>();
//        if (!readPermissionGanted) {
//            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        if (!permissionRequest.isEmpty()) {
//            PermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!Utils.isPermission(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("All File Permission")
                    .setMessage("Due to Android 11 restrictions, this app requires all files permission")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            takePermission();
                        }
                    })
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        } else {
//            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    private void takePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 101);
            }
        } else {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            },101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == 101) {
                boolean readExt = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!readExt) {
                    takePermission();
                }
            }
        }
    }
}