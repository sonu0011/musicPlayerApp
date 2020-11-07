package com.sonu.musicplayer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sonu.musicplayer.fragments.PlayListFragment;
import com.sonu.musicplayer.model.Playlist;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    static ArrayList<MusicFiles> albums = new ArrayList<>();
    public static ArrayList<Playlist> playlists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();
        initViewPager();
    }

    private void permission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 121);
        } else {
            musicFiles = getAllAudio(this);
            playlists = getPlaylists(this);
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 121) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                musicFiles = getAllAudio(this);
                playlists = getPlaylists(this);
            } else
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 121);
        }
    }

    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrament(new SongsFragment(), "Songs");
        viewPagerAdapter.addFrament(new AlbumsFragment(), "Albums");
        viewPagerAdapter.addFrament(new PlayListFragment(), "Playlist");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragmentArrayList;
        private ArrayList<String> fragmentTitles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            fragmentArrayList = new ArrayList<>();
            fragmentTitles = new ArrayList<>();
        }

        private void addFrament(Fragment fragment, String title) {
            fragmentArrayList.add(fragment);
            fragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }

    private ArrayList<MusicFiles> getAllAudio(Context context) {
        ArrayList<MusicFiles> list = new ArrayList<>();
        ArrayList<String> duplicate = new ArrayList<>();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
        };


        Cursor cursor = context.getContentResolver().query(musicUri, projection, null,
                null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration);
                list.add(musicFiles);

                if (!duplicate.contains(album)) {
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return list;
    }

    private ArrayList<Playlist> getPlaylists(Context context) {
        Uri playListUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ArrayList<Playlist> temp = new ArrayList<>();

        String[] playListprojection = {
                BaseColumns._ID,
                MediaStore.Audio.PlaylistsColumns.NAME,
        };

        Cursor cursor1 = context.getContentResolver().query(playListUri, playListprojection, null,
                null, null);

        if (cursor1 != null) {
            while (cursor1.moveToNext()) {
                String id = cursor1.getString(0);
                String name = cursor1.getString(1);
                Playlist playlist = new Playlist(id, name);
                temp.add(playlist);
            }
            cursor1.close();
        }

        return temp;


    }
}