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

import com.example.textile.MainActivity;
import com.example.textile.R;
import com.example.textile.Song;

import java.io.Serializable;
import java.util.ArrayList;

public class RecommenderSystemAdapter extends ArrayAdapter<Song> {
    public RecommenderSystemAdapter(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }

        TextView nameTestView = convertView.findViewById(R.id.item_list_name_textview);
        nameTestView.setText(getItem(position).getmName());

        TextView titleTestView = convertView.findViewById(R.id.item_list_title_textview);
        titleTestView.setText(getItem(position).getmTitle());

        convertView.findViewById(R.id.list_item).setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), MainActivity.class);

            ArrayList<Song> allSongsArrayList = new ArrayList<>();
            for (int i = 0; i < getCount(); i++) {
                allSongsArrayList.add(getItem(i));
            }

            if (intent != null) {
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) allSongsArrayList);
                intent.putExtra("FOR_PLAYING", args);
                intent.putExtra("AUDIO_INDEX",position);

                MainActivity.getInstance().finish();
                getContext().startActivity(intent);
            }
        });


        return convertView;
    }
}
