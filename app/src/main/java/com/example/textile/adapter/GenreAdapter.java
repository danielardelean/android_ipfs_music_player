package com.example.textile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.textile.Album;
import com.example.textile.Genre;
import com.example.textile.R;

import java.util.ArrayList;
import java.util.Random;

public class GenreAdapter extends ArrayAdapter<Genre> {
    public GenreAdapter(Context context, ArrayList<Genre> songs) {
        super(context,0, songs);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_genre, parent, false);
            Random rnd = new Random();

            TextView firstTextView = convertView.findViewById(R.id.genre_name_textview);
            firstTextView.setText(getItem(position).getNameGenre());
            firstTextView.setBackgroundColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        }
        return convertView;
    }
}
