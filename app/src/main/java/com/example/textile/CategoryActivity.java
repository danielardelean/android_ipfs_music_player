package com.example.textile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.textile.fragments.AlbumFragment;
import com.example.textile.fragments.ArtistFragment;
import com.example.textile.fragments.GenreFragment;
import com.example.textile.fragments.PlaylistFragment;
import com.example.textile.fragments.RecommenderSystemFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class CategoryActivity extends AppCompatActivity {
    //Firebase data retrieve
    public static ArrayList<Artist> artists;
    public static ArrayList<Song> userAudioStreamingHistory;

    private ViewPager categoryViewPager;
    private TabLayout categoryTabLayout;

    private AlbumFragment albumFragment;
    private ArtistFragment artistFragment;
    private GenreFragment genreFragment;
    private PlaylistFragment playlistFragment;
    private RecommenderSystemFragment recommenderSystemFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryViewPager = findViewById(R.id.view_pager);
        categoryTabLayout = findViewById(R.id.tab_layout);
        findViewById(R.id.category_back_button).setOnClickListener(view -> finish());

        albumFragment = new AlbumFragment();
        artistFragment = new ArtistFragment();
        genreFragment = new GenreFragment();
        playlistFragment = new PlaylistFragment();
        recommenderSystemFragment = new RecommenderSystemFragment();

        categoryTabLayout.setupWithViewPager(categoryViewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(artistFragment, "ARTISTS");
        viewPagerAdapter.addFragment(albumFragment, "ALBUMS");
        viewPagerAdapter.addFragment(genreFragment, "GENRE");
        viewPagerAdapter.addFragment(playlistFragment, "PL");
        viewPagerAdapter.addFragment(recommenderSystemFragment, "RS");
        categoryViewPager.setAdapter(viewPagerAdapter);


        categoryTabLayout.getTabAt(0).setIcon(R.drawable.ic_artist);
        categoryTabLayout.getTabAt(1).setIcon(R.drawable.ic_album);
        categoryTabLayout.getTabAt(2).setIcon(R.drawable.ic_genre);
        categoryTabLayout.getTabAt(3).setIcon(R.drawable.ic_playlist);
        categoryTabLayout.getTabAt(4).setIcon(R.drawable.ic_recommender_system_white_24dp);
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
