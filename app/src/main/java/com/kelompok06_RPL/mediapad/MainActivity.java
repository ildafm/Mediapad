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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Switch;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bnvNavigationView;
    private FrameLayout flContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

}