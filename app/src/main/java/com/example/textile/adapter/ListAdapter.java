package com.example.textile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.textile.Album;
import com.example.textile.R;
import com.example.textile.Song;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Song> {
    public ListAdapter(Context context, ArrayList<Song> songs) {
        super(context,0, songs);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);

            TextView firstTextView = convertView.findViewById(R.id.list_name_textview);
            firstTextView.setText(getItem(position).getmName());

            TextView title = convertView.findViewById(R.id.list_title_textview);
            title.setText(getItem(position).getmTitle());

            convertView.findViewById(R.id.list_item).setOnClickListener(view -> System.out.println("Just"));

        }
        return convertView;
    }
}
