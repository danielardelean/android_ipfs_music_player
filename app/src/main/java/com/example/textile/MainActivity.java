package com.example.textile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.textile.textile.Textile;

public class MainActivity extends AppCompatActivity {
    static final String ACCOUNT_SEED = "SWjTdgps4E3bWxcog798LuRnvGK6Rnt6oLQoUH4sHY2MV9L7";
    static String usernameApplication = "ardelean@gmail.com";
    public Player player;
    static MainActivity activity;

    public static MainActivity getInstance() {
        return activity;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            if (player.mediaPlayer != null) {
                player.stopMedia();
                player.releaseMediaPlayer();
            }
            player = null;
        }
        //  destroyTextile();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;


        if (getIntent().getBundleExtra("FOR_PLAYING") != null) {
            ArrayList<Song> songs = (ArrayList<Song>) getIntent().getBundleExtra("FOR_PLAYING").getSerializable("ARRAYLIST");
            int audioIndex = getIntent().getIntExtra("AUDIO_INDEX", 0);

            player = new Player(songs, this, audioIndex);
            try {
                player.playMedia();
                Thread.sleep(500);
                player.playMedia();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        initializeLocalDB();
    }

    private void destroyTextile() {
        Textile.instance().destroy();
    }

    public void playSong(View view) {
        if (player != null && player.mediaPlayer != null) {
            player.playMedia();
        }
    }

    public void pauseSong(View view) {
        if (player != null) {
            player.pauseMedia();
        }
    }

    public void prevSong(View view) {
        if (player != null) {
            player.updateSkipStreamingHistoryFirebase();
            player.skipToPrevious();
        }
    }

    public void nextSong(View view) {
        if (player != null) {
            player.updateSkipStreamingHistoryFirebase();
            player.skipToNext();
        }
    }

    public void settingsActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    public void playListActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
        startActivity(intent);
    }

    private void initializeLocalDB() {
        //User streaming history retrieve from Firebase Realtime
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CategoryActivity.userAudioStreamingHistory = new ArrayList<Song>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    System.out.println();
                    if (ds.child("email").getValue(String.class).compareTo(usernameApplication) == 0) {

                        for (DataSnapshot ds1 : ds.child("streamingHistory").getChildren()) {
                            String title = ds1.child("title").getValue(String.class);
                            String name = ds1.child("name").getValue(String.class);
                            String hash = ds1.child("hash").getValue(String.class);
                            String genre = ds1.child("genre").getValue(String.class);
                            int skipTimes = ((ds1.child("skipTimes").getValue(Integer.class) == null) ?
                                    0 : ds1.child("skipTimes").getValue(Integer.class));
                            int playingTimes = ((ds1.child("playingTimes").getValue(Integer.class) == null) ?
                                    0 : ds1.child("playingTimes").getValue(Integer.class));
                            boolean likeState = ((ds1.child("liked").getValue(Boolean.class) == null) ?
                                    false : ds1.child("liked").getValue(Boolean.class));

                            CategoryActivity.userAudioStreamingHistory.add(new Song(title, name, hash, skipTimes, playingTimes, genre, likeState));
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Artists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CategoryActivity.artists = new ArrayList();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Artist artist = new Artist(ds.child("artistName").getValue(String.class));

                    for (DataSnapshot ds1 : ds.child("Albums").getChildren()) {
                        Album album = new Album(ds1.child("albumName").getValue(String.class));

                        for (DataSnapshot ds2 : ds1.child("Songs").getChildren()) {
                            album.addSong(new Song(ds2.child("title").getValue(String.class),
                                    ds2.child("name").getValue(String.class),
                                    ds2.child("hash").getValue(String.class),
                                    ds2.child("genre").getValue(String.class)));
                        }
                        artist.addAlbum(album);
                    }
                    CategoryActivity.artists.add(artist);
                }
                System.out.println("Firebase DB data retrieve");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

















