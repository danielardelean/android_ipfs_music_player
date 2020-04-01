package com.example.textile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.textile.Album;
import com.example.textile.Artist;
import com.example.textile.CategoryActivity;
import com.example.textile.R;
import com.example.textile.Song;
import com.example.textile.adapters.RecommenderSystemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class RecommenderSystemFragment extends Fragment {
    private Context mContext;
    private HashMap<String, Integer> genreStatistics;
    private ArrayList<Song> unlistenedSongs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public RecommenderSystemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommender_system, container, false);

        if (CategoryActivity.userAudioStreamingHistory.size() > 0) {
            RecommenderSystemAdapter itemsAdapter = new RecommenderSystemAdapter(mContext, recommendedAlgorithm(CategoryActivity.userAudioStreamingHistory));
            ListView listView = view.findViewById(R.id.listview_recommender_system_fragment);
            listView.setAdapter(itemsAdapter);
        }
        return view;
    }

    private ArrayList<Song> recommendedAlgorithm(ArrayList<Song> songArrayList) {
        genreStatistics = new HashMap<>();
        for (int i = 0; i < songArrayList.size(); i++) {
            updateGenreStatistics(songArrayList.get(i).getmGenre());

            float skipTimes = (songArrayList.get(i).getmSkipTimes() > 0) ? songArrayList.get(i).getmSkipTimes() : 1;
            float likeLogic = (songArrayList.get(i).ismLike()) ? 10 : 1;
            float interestLevel = likeLogic / skipTimes;
            float songWeight;

            if (songArrayList.get(i).getmPlayingTimes() > 0) {
                if (songArrayList.get(i).getmPlayingTimes() > 5) {
                    songWeight = (float) ((0.6 * interestLevel) + (0.4 * songArrayList.get(i).getmPlayingTimes()));
                } else {
                    songWeight = (float) ((0.4 * interestLevel) + (0.6 * songArrayList.get(i).getmPlayingTimes()));
                }
            } else {
                songWeight = interestLevel;
            }

            songArrayList.get(i).setWeight(songWeight);
        }
        initArrayUnListenedSongs();
        Collections.sort(songArrayList, new Sort());

        //Shrink the size to 10
        int k = songArrayList.size();
        if (k > 10)
            songArrayList.subList(10, k).clear();


        //Shink and add songs
        if (unlistenedSongs.size() > 10) {
            unlistenedSongs.subList(10, unlistenedSongs.size()).clear();
        }
        songArrayList.addAll(unlistenedSongs);

        return songArrayList;
    }

    private void updateGenreStatistics(String key) {
        int currentNo;
        if (genreStatistics.containsKey(key)) {
            currentNo = genreStatistics.get(key);
        } else {
            currentNo = 0;
        }
        genreStatistics.put(key, ++currentNo);
    }

    private void initArrayUnListenedSongs() {
        unlistenedSongs = new ArrayList<>();
        String mostLikedGenre = genreStatistics.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
        System.out.println(genreStatistics+""+mostLikedGenre+"MOOOST");

        for (Artist artist : CategoryActivity.artists) {
            for (Album album : artist.getAlbumArrayList()) {
                for (Song song : album.getmAlbumSongsArraylist()) {
                    if (!containsSongProperties(CategoryActivity.userAudioStreamingHistory, song.getmName(), song.getmTitle()) &&
                            song.getmGenre().compareTo(mostLikedGenre)==0) {
                        unlistenedSongs.add(song);
                    }
                }
            }
        }
    }

    public boolean containsSongProperties(final List<Song> list, final String name, final String title) {
        return list.stream().anyMatch(o -> o.getmName().equals(name)) &&
                list.stream().anyMatch(o -> o.getmTitle().equals(title));
    }
}

class Sort implements Comparator<Song> {
    @Override
    public int compare(Song o1, Song o2) {
        return Float.compare(o2.getWeight(), o1.getWeight());
    }
}