package com.example.textile.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.textile.Album;
import com.example.textile.MainActivity;
import com.example.textile.R;
import com.example.textile.adapter.AlbumAdapter;

import java.util.ArrayList;

public class AlbumFragment extends Fragment  {
    private ArrayList<Album> list;
    private Context mContext;

    public AlbumFragment() {
    }

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        list = new ArrayList<Album>();
        for(int i=0;i<MainActivity.artists.size();i++){
            for(int j=0;j<MainActivity.artists.get(i).getAlbumArrayList().size();j++) {
                list.add(MainActivity.artists.get(i).getAlbumArrayList().get(j));
            }
        }

        AlbumAdapter itemsAdapter = new AlbumAdapter(mContext, list);
        GridView gridView = view.findViewById(R.id.list_album);
        gridView.setAdapter(itemsAdapter);

        return view;
    }
}
