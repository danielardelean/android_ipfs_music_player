package com.example.textile.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.textile.Genre;
import com.example.textile.R;
import com.example.textile.adapter.GenreAdapter;

import java.util.ArrayList;

public class GenreFragment extends Fragment  {
    private ArrayList<Genre> list;
    private Context mContext;

    public GenreFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_genre, container, false);

        list = new ArrayList<Genre>();
        list.add(new Genre("Pop"));
        list.add(new Genre("Rock"));
        list.add(new Genre("Indie"));
        list.add(new Genre("Eloctro"));

        GenreAdapter itemsAdapter = new GenreAdapter(mContext, list);
        GridView gridView = view.findViewById(R.id.list_genre);
        gridView.setAdapter(itemsAdapter);

        return view;
    }
}
