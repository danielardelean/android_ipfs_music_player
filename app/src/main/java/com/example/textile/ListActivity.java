package com.example.textile;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.textile.adapters.ListAdapter;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ArrayList<Song> songs = (ArrayList<Song>) getIntent().getBundleExtra("ALL_SONGS").getSerializable("ARRAYLIST");

        ListAdapter itemsAdapter = new ListAdapter(this, songs);
        ListView listView = findViewById(R.id.list_activity_list);
        listView.setAdapter(itemsAdapter);
    }

    public void backToCategory(View view) {
        finish();
    }
}

