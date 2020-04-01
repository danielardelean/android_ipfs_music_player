package com.example.textile.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.textile.Album;
import com.example.textile.ListActivity;
import com.example.textile.R;
import com.example.textile.Song;

import java.io.Serializable;
import java.util.ArrayList;

public class AlbumAdapter extends ArrayAdapter<Album> {
    public AlbumAdapter(Context context, ArrayList<Album> songs) {
        super(context, 0, songs);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_album, parent, false);
        }
        TextView firstTextView = convertView.findViewById(R.id.album_name_textview);
        firstTextView.setText(getItem(position).getmAlbumName());

        convertView.findViewById(R.id.list_item).setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ListActivity.class);

            ArrayList<Song> allSongsArrayList = new ArrayList<>();
            allSongsArrayList.addAll(getItem(position).getmAlbumSongsArraylist());

            if (intent != null) {
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) allSongsArrayList);

                intent.putExtra("ALL_SONGS", args);
                getContext().startActivity(intent);
            }

        });

        return convertView;
    }
}
