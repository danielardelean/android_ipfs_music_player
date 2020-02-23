package com.example.textile;

import android.content.Context;

import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import io.textile.pb.Model;
import io.textile.pb.QueryOuterClass;
import io.textile.textile.BaseTextileEventListener;
import io.textile.textile.Handlers;
import io.textile.textile.Schemas;
import io.textile.textile.Textile;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTextile();

        Button simpleButton1 = (Button) findViewById(R.id.simpleButton);
        simpleButton1.setOnClickListener(view -> {

            try {
//                System.out.println(Textile.instance().threads.peers("12D3KooWA4sraEADfR2sCWAETXbJFDxsi3wLeBcdpzadEDWW4h75").getItemsCount());
//                System.out.println(Textile.instance().threads.get("12D3KooWA4sraEADfR2sCWAETXbJFDxsi3wLeBcdpzadEDWW4h75").getPeerCount());

               // getFile();
                getIPFSFile();



//                textView.setText(a+"j"); //set text for text view
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        Button simpleButton = (Button) findViewById(R.id.simpleButton1);
        simpleButton.setOnClickListener(view -> {
            try {
                Textile.instance().threads.snapshot();
                Textile.instance().account.sync(QueryOuterClass.QueryOptions.getDefaultInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void initTextile() {
        try {
            //Create and lunch the textile node
            Context ctx = getApplicationContext();

            final File filesDir = ctx.getFilesDir();
            final String path = new File(filesDir, "textile-go").getAbsolutePath();

            if (!Textile.isInitialized(path)) {
                Textile.initialize(path, "SWjTdgps4E3bWxcog798LuRnvGK6Rnt6oLQoUH4sHY2MV9L7", true,false);
            }

            Textile.launch(ctx, path, true);
            class MyEventListener extends BaseTextileEventListener { }
            Textile.instance().addEventListener(new MyEventListener());


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void destroyTextile() {
        Textile.instance().destroy();
    }



    private void getFile(){
        Textile.instance().files.content("QmeTSQLsHJgkZiMB8jk1v4W5yUBjZg7Uyc7qZAG3h65TWw", new Handlers.DataHandler() {
            @Override
            public void onComplete(byte[] data, String media) {
                System.out.println(data.toString());
            }

            @Override
            public void onError(Exception e) {
                System.out.println(e);
            }
        });
    }

    private void getIPFSFile(){
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




    private MediaPlayer mediaPlayer = new MediaPlayer();
    private void playMp3(byte[] mp3SoundByteArray) {
        try {
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
}
