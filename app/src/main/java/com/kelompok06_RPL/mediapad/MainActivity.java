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

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bnvNavigationView;
    private FrameLayout flContainer;

    ActivityResultLauncher<String[]> PermissionResultLauncher;
    private boolean readPermissionGanted = false;
    private boolean locationPermissionGranted = false;
    private boolean recordPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE)!= null){
                    readPermissionGanted = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (result.get(Manifest.permission.ACCESS_FINE_LOCATION)!= null){
                    locationPermissionGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (result.get(Manifest.permission.RECORD_AUDIO)!= null){
                    recordPermissionGranted = result.get(Manifest.permission.RECORD_AUDIO);
                }
            }
        });

        requestPermission();

        bukaFragment(new VideoFragment());
        getSupportActionBar().setTitle("Video");

        bnvNavigationView = findViewById(R.id.bnv_navigasi_bottom);
        flContainer = findViewById(R.id.fl_container);

        bnvNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment FR;

                switch (item.getItemId()) {
                    case R.id.menu_video:
                        bukaFragment(new VideoFragment());
                        getSupportActionBar().setTitle("Video");
                        return true;

                    case R.id.menu_musik:
                        bukaFragment(new MusikFragment());
                        getSupportActionBar().setTitle("Musik");
                        return true;

                    case R.id.menu_playlist:
                        bukaFragment(new PlaylistFragment());
                        getSupportActionBar().setTitle("Playlist");
                        return true;

                    case R.id.menu_browse:
                        bukaFragment(new BrowseFragment());
                        getSupportActionBar().setTitle("Browse");
                        return true;
                }
                return false;
            }
        });

    }

    private void bukaFragment(Fragment FRG){
        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction FT = FM.beginTransaction();
        FT.replace(R.id.fl_container, FRG);
        FT.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void requestPermission(){
        readPermissionGanted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
        locationPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
        recordPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<String>();
        if (!readPermissionGanted){
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!locationPermissionGranted){
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!recordPermissionGranted){
            permissionRequest.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!permissionRequest.isEmpty()){
            PermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }

}