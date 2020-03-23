package com.example.textile;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.textile.music_service.Audio;
import com.example.textile.music_service.MediaPlayerService;
import com.example.textile.music_service.StorageUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

import io.textile.textile.BaseTextileEventListener;
import io.textile.textile.Textile;

public class MainActivity extends AppCompatActivity {

    private MediaPlayerService player;
    boolean serviceBound = false;

    ArrayList<Audio> audioList;

    //Used to send broadcasts intents when the user wants to play new audio
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.textile.PlayNewAudio";
    public static final String Broadcast_PAUSE_AUDIO = "com.example.textile.PauseAudio";
    public static final String Broadcast_NEXT_AUDIO = "com.example.textile.NextAudio";
    public static final String Broadcast_PREV_AUDIO = "com.example.textile.PrevAudio";

    TextView songTitleName, songArtistName;

    /**
     * This method will load the songs for playing
     * Needs to be edited for the app purpose
     */
    private void loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                // Save to audioList
                audioList.add(new Audio(data, title, album, artist));
            }
        }
        cursor.close();
    }


    /**
     * Binding this client (player variable) to the AudioPlayer Service
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    /**
     * Triggers the running service based on the playing list created before
     */
    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudio(audioList);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }


    /**
     * Stopping the music player service and the Textile node
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
        destroyTextile();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaPlayerService.title = findViewById(R.id.song_title_text_view);
        MediaPlayerService.artist = findViewById(R.id.song_artist_text_view);
        initTextile();

        initializeLocalDB();
    }


    /**
     * Initialize the IPFS Textile node by using the secret hash of the account seed
     * If node is already initialized, this call will be done just to start the node
     */
    private void initTextile() {
        try {
            Context ctx = getApplicationContext();

            final File filesDir = ctx.getFilesDir();
            final String path = new File(filesDir, "textile-go").getAbsolutePath();
            if (!Textile.isInitialized(path)) {
                Textile.initialize(path, "SWjTdgps4E3bWxcog798LuRnvGK6Rnt6oLQoUH4sHY2MV9L7", true, false);
            }

            Textile.launch(ctx, path, true);
            class MyEventListener extends BaseTextileEventListener {
            }
            Textile.instance().addEventListener(new MyEventListener());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void destroyTextile() {
        Textile.instance().destroy();
    }


    public void playSong(View view) {
        loadAudio();
        playAudio(0);
    }

    public void pauseSong(View view) {
        Intent broadcastIntent = new Intent(Broadcast_PAUSE_AUDIO);
        sendBroadcast(broadcastIntent);
    }

    public void prevSong(View view) {
        Intent broadcastIntent = new Intent(Broadcast_PREV_AUDIO);
        sendBroadcast(broadcastIntent);
    }

    public void nextSong(View view) throws InterruptedException {
        Intent broadcastIntent = new Intent(Broadcast_NEXT_AUDIO);
        sendBroadcast(broadcastIntent);
    }

    public void settingsActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    public void playListActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
        startActivity(intent);
    }


    public static ArrayList<Artist> artists;
    private void initializeLocalDB() {

        FirebaseDatabase.getInstance().getReference().child("Artists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                artists = new ArrayList<Artist>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Artist artist = new Artist(ds.child("artistName").getValue(String.class));

                    for (DataSnapshot ds1 : ds.child("Albums").getChildren()) {
                        Album album = new Album(ds1.child("albumName").getValue(String.class));

                        for (DataSnapshot ds2 : ds1.child("Songs").getChildren()) {
                            album.addSong(new Song(ds2.child("title").getValue(String.class),
                                    ds2.child("name").getValue(String.class),
                                    ds2.child("hash").getValue(String.class)));
                        }
                        artist.addAlbum(album);
                    }
                    artists.add(artist);
                }
                System.out.println("AAAA"+artists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}




