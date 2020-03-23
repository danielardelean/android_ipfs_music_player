package com.example.textile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.textile.fragment.AlbumFragment;
import com.example.textile.fragment.ArtistFragment;
import com.example.textile.fragment.GenreFragment;
import com.example.textile.fragment.PlaylistFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class CategoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private AlbumFragment albumFragment;
    private PlaylistFragment playlistFragment;
    private ArtistFragment artistFragment;
    private GenreFragment genreFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        albumFragment = new AlbumFragment();
        playlistFragment = new PlaylistFragment();
        artistFragment = new ArtistFragment();
        genreFragment = new GenreFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(artistFragment, "ARTISTS");
        viewPagerAdapter.addFragment(albumFragment, "ALBUMS");
        viewPagerAdapter.addFragment(genreFragment, "GENRE");
        viewPagerAdapter.addFragment(playlistFragment, "PLAYLISTS");
        viewPager.setAdapter(viewPagerAdapter);


        tabLayout.getTabAt(0).setIcon(R.drawable.ic_person);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_album);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_view_agenda);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_playlist_play);

    }

    public void backButton(View view) {
        finish();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();


        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
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
            return fragmentTitle.get(position);
        }
    }
}
