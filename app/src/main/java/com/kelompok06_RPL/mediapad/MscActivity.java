package com.kelompok06_RPL.mediapad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MscActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private BottomNavigationView bnvNavigationView;
    static ArrayList<MusicFiles> musicFiles;
    static boolean shuffelBoolean = false, repeatBoolean = false;
    static ArrayList<MusicFiles> albumSong = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msc);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_primary_variant)));
        getSupportActionBar().setTitle("Music");
        bnvNavigationView = findViewById(R.id.bnv_navigasi_bottom);
        bnvNavigationView.setSelectedItemId(R.id.menu_musik);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_primary_variant)));
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
                        startActivity(new Intent(getApplicationContext(), MscActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.menu_playlist:
                        startActivity(new Intent(getApplicationContext(), MusikActivity.class));
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
        initViewPager();
        musicFiles = getAllAudio(this);
    }

    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tab);
        viewPagerAdapter viewPagerAdapters = new viewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapters.addFragment(new SongsFragment(),"Songs");
        viewPagerAdapters.addFragment(new AlbumFragment(),"Album");
        viewPager.setAdapter(viewPagerAdapters);
        tabLayout.setupWithViewPager(viewPager);
    }



    public static class viewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        public viewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public ArrayList<MusicFiles> getAllAudio(Context context) {
        ArrayList<String> duplicate = new ArrayList<>();
        ArrayList<MusicFiles> temArraylist = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
        };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);
                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration, id);
                Log.e("Path : " + path,"Album : " + album);
                temArraylist.add(musicFiles);
                if (!duplicate.contains(album)) {
                    albumSong.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return temArraylist;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_menu_search,menu);
        MenuItem search = menu.findItem(R.id.search_video);
        MenuItem refresh = menu.findItem(R.id.refresh_file);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input = newText.toLowerCase();
        ArrayList<MusicFiles> myFiles = new ArrayList<>();
        for (MusicFiles song : musicFiles) {
            if (song.getTitle().toLowerCase().contains(input)) {
                myFiles.add(song);
            }
        }
        SongsFragment.mscAdapter.updateList(myFiles);
        return true;
    }
}