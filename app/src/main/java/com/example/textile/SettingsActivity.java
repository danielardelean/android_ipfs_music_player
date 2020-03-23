package com.example.textile;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.textile.fragment.SettingsFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.textile.pb.QueryOuterClass;
import io.textile.textile.Handlers;
import io.textile.textile.Textile;

public class SettingsActivity extends AppCompatActivity {


    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (R.id.settings_frame_layout != 0) {
            if (savedInstanceState != null)
                return;
            getFragmentManager().beginTransaction().add(R.id.settings_frame_layout, new SettingsFragment()).commit();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void testTextile(View view) {
        try {
            getFile();
            // getIPFSFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFile() {
        Textile.instance().files.content("QmVKgGhphJEXaGmrd9zhDPRdtvF23szgKnZNN6U5nv8v5d", new Handlers.DataHandler() {
            @Override
            public void onComplete(byte[] data, String media) {
                playMp3(data);
            }

            @Override
            public void onError(Exception e) {
                System.out.println(e);
            }
        });
    }

    private void getIPFSFile() {
        Textile.instance().ipfs.dataAtPath("QmeUoHVAUwf22fHV1Gtp5Y4rcL8mDRmSn7Gwcezu2Dsd4w", new Handlers.DataHandler() {
            @Override
            public void onComplete(byte[] data, String media) {
                playMp3(data);
            }

            @Override
            public void onError(Exception e) {
                System.out.println(e);
            }
        });
    }

    private void playMp3(byte[] mp3SoundByteArray) {
        try {
             MediaPlayer mediaPlayer = new MediaPlayer();
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();


            mediaPlayer.reset();

            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            String s = ex.toString();
            ex.printStackTrace();
        }
    }

    public void synchronizeTextile(View view) {
        try {
            Textile.instance().threads.snapshot();
            Textile.instance().account.sync(QueryOuterClass.QueryOptions.getDefaultInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
