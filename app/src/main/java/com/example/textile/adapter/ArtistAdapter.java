package com.example.textile.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.textile.Artist;
import com.example.textile.ListActivity;
import com.example.textile.R;
import com.example.textile.Song;

import java.io.Serializable;
import java.util.ArrayList;

public class ArtistAdapter extends ArrayAdapter<Artist> {
    public ArtistAdapter(Context context, ArrayList<Artist> songs) {
        super(context, 0, songs);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_artist, parent, false);

            TextView firstTextView = convertView.findViewById(R.id.artist_name_textview);
            firstTextView.setText(getItem(position).getArtistName());

            convertView.findViewById(R.id.list_item).setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), ListActivity.class);

                ArrayList<Song> allSongsArrayList = new ArrayList<>();
                for (int i = 0; i < getItem(position).getAlbumArrayList().size(); i++) {
                    allSongsArrayList.addAll(getItem(position).getAlbumArrayList().get(i).getmAlbumSongsArraylist());
                }

                if (intent != null){
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST",(Serializable)allSongsArrayList);

                    intent.putExtra("ALL_SONGS", args);
                    getContext().startActivity(intent);
                }

            });
        }
        return convertView;
    }
}
