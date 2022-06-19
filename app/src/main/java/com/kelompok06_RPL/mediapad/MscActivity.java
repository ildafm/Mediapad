package com.kelompok06_RPL.mediapad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    public static final String MY_PREF = "my pref";
    private BottomNavigationView bnvNavigationView;
    static ArrayList<MusicFiles> musicFiles;
    static boolean shuffelBoolean = false, repeatBoolean = false;
    static ArrayList<MusicFiles> albumSong = new ArrayList<>();
    String sort_Order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msc);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.color_primary_variant)));
        getSupportActionBar().setTitle("Music");
        bnvNavigationView = findViewById(R.id.bnv_navigasi_bottom);
        bnvNavigationView.setSelectedItemId(R.id.menu_musik);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
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
        SharedPreferences preferences = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        String sort_value = preferences.getString("sort", "xyz");
        ArrayList<String> duplicate = new ArrayList<>();
        ArrayList<MusicFiles> temArraylist = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        if (sort_value.equals("sortName")) {
            sort_Order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
        } else if (sort_value.equals("sortSize")) {
            sort_Order = MediaStore.MediaColumns.SIZE + " DESC";
        } else if (sort_value.equals("sortDate")) {
            sort_Order = MediaStore.MediaColumns.DATE_ADDED + " DESC";
        } else {
            sort_Order = MediaStore.Video.Media.DURATION + " DESC";
        }
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
        };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, sort_Order);
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
}