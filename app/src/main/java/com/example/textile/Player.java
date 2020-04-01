package com.example.textile;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.textile.textile.Handlers;
import io.textile.textile.Textile;

class Player {
    private TextView artist, title;
    private ImageView likeImageView;

    private ArrayList<Song> songArrayList;
    MediaPlayer mediaPlayer;
    private AudioManager myAudioManager;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;
    private Context context;

    private int resumePosition;
    private int audioIndex;
    private boolean state = false;

    private FirebaseDatabase database;
    private DatabaseReference mRef;

    Player(ArrayList<Song> songArrayList, MainActivity context, int index) {
        this.songArrayList = songArrayList;
        this.context = context;
        this.audioIndex = index;
        mediaPlayer = new MediaPlayer();
        database = FirebaseDatabase.getInstance();

        title = context.findViewById(R.id.song_title_text_view);
        artist = context.findViewById(R.id.song_artist_text_view);
        likeImageView = context.findViewById(R.id.activity_main_like_image_button);
        likeImageView.setOnClickListener(view -> {
            updateLikeStreamingHistoryFirebase(songArrayList.get(audioIndex).ismLike());
            if (songArrayList.get(audioIndex).ismLike()) {
                songArrayList.get(audioIndex).setmLike(false);
                updateLikeStreamingHistoryFirebase(false);
                likeImageView.setImageResource(R.drawable.ic_favorite);
            } else {
                songArrayList.get(audioIndex).setmLike(true);
                updateLikeStreamingHistoryFirebase(true);
                likeImageView.setImageResource(R.drawable.ic_favorite_filled_white_24dp);
            }
        });

        myAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        afChangeListener = focusChange -> {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                mediaPlayer.pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                stopMedia();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                float v = (float) 0.5;
                mediaPlayer.setVolume(v, v);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mediaPlayer.start();
                mediaPlayer.setVolume(1, 1);
            }
        };
    }

    void playMedia() {
        if (!state) {
            getFileFromTextileNetwork(songArrayList.get(audioIndex).getmHash());
            state = true;
        } else {
            resumeMedia();
        }
    }

    void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            releaseMediaPlayer();
        }
    }

    void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    void skipToNext() {
        if (audioIndex == songArrayList.size() - 1) {
            audioIndex = 0;
        } else {
            ++audioIndex;
        }

        stopMedia();
        getFileFromTextileNetwork(songArrayList.get(audioIndex).getmHash());
    }

    void skipToPrevious() {
        if (audioIndex == 0) {
            audioIndex = songArrayList.size() - 1;
        } else {
            --audioIndex;
        }

        stopMedia();
        getFileFromTextileNetwork(songArrayList.get(audioIndex).getmHash());
    }

    void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        myAudioManager.abandonAudioFocus(afChangeListener);
    }

    private void getFileFromTextileNetwork(String hash) {
        title.setText(songArrayList.get(audioIndex).getmName());
        artist.setText(songArrayList.get(audioIndex).getmTitle());
        if (songArrayList.get(audioIndex).ismLike()) {
            likeImageView.setImageResource(R.drawable.ic_favorite_filled_white_24dp);
        }else {
            likeImageView.setImageResource(R.drawable.ic_favorite);
        }

        Textile.instance().files.content(hash, new Handlers.DataHandler() {
            @Override
            public void onComplete(byte[] data, String media) {
                playStreamingMediaFile(data);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void playStreamingMediaFile(byte[] mp3SoundByteArray) {
        try {
            File tempMp3 = File.createTempFile("file", "mp3", context.getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(mp3SoundByteArray);
            fos.close();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            FileInputStream fis = new FileInputStream(tempMp3);
            try {
                mediaPlayer.setDataSource(fis.getFD());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.prepare();
            mediaPlayer.start();

            myAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                updatePlayingTimesStreamingHistoryFirebase();
                skipToNext();
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void updateSkipStreamingHistoryFirebase() {
        boolean songAlreadyPlayed = false;

        String name = null, title = null, hash = null, genre = null;
        int  playingTimes = 0;
        boolean likeState = false;
        int currentSkipTimes = 0;

        for (int i = 0; i < CategoryActivity.userAudioStreamingHistory.size(); i++) {
            if (CategoryActivity.userAudioStreamingHistory.get(i).getmName().compareTo(songArrayList.get(audioIndex).getmName()) == 0) {

                songAlreadyPlayed = true;
                currentSkipTimes = CategoryActivity.userAudioStreamingHistory.get(i).getmSkipTimes();
                currentSkipTimes++;

                genre = CategoryActivity.userAudioStreamingHistory.get(i).getmGenre();
                hash = CategoryActivity.userAudioStreamingHistory.get(i).getmHash();
                name = CategoryActivity.userAudioStreamingHistory.get(i).getmName();
                playingTimes = CategoryActivity.userAudioStreamingHistory.get(i).getmPlayingTimes();
                title = CategoryActivity.userAudioStreamingHistory.get(i).getmTitle();
                likeState = CategoryActivity.userAudioStreamingHistory.get(i).ismLike();

                break;
            }
        }

        if (!songAlreadyPlayed) {
            currentSkipTimes = songArrayList.get(audioIndex).getmSkipTimes();
            currentSkipTimes++;
            songArrayList.get(audioIndex).setmSkipTimes(currentSkipTimes);

            genre = songArrayList.get(audioIndex).getmGenre();
            hash = songArrayList.get(audioIndex).getmHash();
            name = songArrayList.get(audioIndex).getmName();
            playingTimes = songArrayList.get(audioIndex).getmPlayingTimes();
            title = songArrayList.get(audioIndex).getmTitle();
        }

        //Update the DB
        mRef = database.getReference().child("Users")
                .child(MainActivity.usernameApplication.split("@")[0])
                .child("streamingHistory")
                .child(title + " - " + name);
        mRef.child("genre").setValue(genre);
        mRef.child("hash").setValue(hash);
        mRef.child("name").setValue(name);
        mRef.child("playingTimes").setValue(playingTimes);
        mRef.child("skipTimes").setValue(currentSkipTimes);
        mRef.child("title").setValue(title);
        mRef.child("liked").setValue(likeState);
    }

    private void updatePlayingTimesStreamingHistoryFirebase() {
        boolean songAlreadyPlayed = false;

        String name = null, title = null, hash = null, genre = null;
        int  skipTimes = 0;
        boolean likeState = false;
        int currentPlayingTimes = 0;

        for (int i = 0; i < CategoryActivity.userAudioStreamingHistory.size(); i++) {
            if (CategoryActivity.userAudioStreamingHistory.get(i).getmName().compareTo(songArrayList.get(audioIndex).getmName()) == 0) {

                songAlreadyPlayed = true;
                currentPlayingTimes = CategoryActivity.userAudioStreamingHistory.get(i).getmPlayingTimes();
                currentPlayingTimes++;

                genre = CategoryActivity.userAudioStreamingHistory.get(i).getmGenre();
                hash = CategoryActivity.userAudioStreamingHistory.get(i).getmHash();
                name = CategoryActivity.userAudioStreamingHistory.get(i).getmName();
                skipTimes = CategoryActivity.userAudioStreamingHistory.get(i).getmSkipTimes();
                title = CategoryActivity.userAudioStreamingHistory.get(i).getmTitle();
                likeState = CategoryActivity.userAudioStreamingHistory.get(i).ismLike();

                break;
            }
        }

        if (!songAlreadyPlayed) {
            currentPlayingTimes = songArrayList.get(audioIndex).getmPlayingTimes();
            currentPlayingTimes++;
            songArrayList.get(audioIndex).setmPlayingTimes(currentPlayingTimes);

            genre = songArrayList.get(audioIndex).getmGenre();
            hash = songArrayList.get(audioIndex).getmHash();
            name = songArrayList.get(audioIndex).getmName();
            skipTimes = songArrayList.get(audioIndex).getmSkipTimes();
            title = songArrayList.get(audioIndex).getmTitle();
        }

        //Update the DB
        mRef = database.getReference().child("Users")
                .child(MainActivity.usernameApplication.split("@")[0])
                .child("streamingHistory")
                .child(title + " - " + name);
        mRef.child("genre").setValue(genre);
        mRef.child("hash").setValue(hash);
        mRef.child("name").setValue(name);
        mRef.child("playingTimes").setValue(currentPlayingTimes);
        mRef.child("skipTimes").setValue(skipTimes);
        mRef.child("title").setValue(title);
        mRef.child("liked").setValue(likeState);
    }

    private void updateLikeStreamingHistoryFirebase(boolean stateLike) {
        boolean songAlreadyPlayed = false;

        String name = null, title = null, hash = null, genre = null;
        int  playingTimes = 0;
        int currentSkipTimes = 0;

        for (int i = 0; i < CategoryActivity.userAudioStreamingHistory.size(); i++) {
            if (CategoryActivity.userAudioStreamingHistory.get(i).getmName().compareTo(songArrayList.get(audioIndex).getmName()) == 0) {

                songAlreadyPlayed = true;
                currentSkipTimes = CategoryActivity.userAudioStreamingHistory.get(i).getmSkipTimes();
                genre = CategoryActivity.userAudioStreamingHistory.get(i).getmGenre();
                hash = CategoryActivity.userAudioStreamingHistory.get(i).getmHash();
                name = CategoryActivity.userAudioStreamingHistory.get(i).getmName();
                playingTimes = CategoryActivity.userAudioStreamingHistory.get(i).getmPlayingTimes();
                title = CategoryActivity.userAudioStreamingHistory.get(i).getmTitle();

                break;
            }
        }

        if (!songAlreadyPlayed) {
            currentSkipTimes = songArrayList.get(audioIndex).getmSkipTimes();
            genre = songArrayList.get(audioIndex).getmGenre();
            hash = songArrayList.get(audioIndex).getmHash();
            name = songArrayList.get(audioIndex).getmName();
            playingTimes = songArrayList.get(audioIndex).getmPlayingTimes();
            title = songArrayList.get(audioIndex).getmTitle();
        }

        //Update the DB
        mRef = database.getReference().child("Users")
                .child(MainActivity.usernameApplication.split("@")[0])
                .child("streamingHistory")
                .child(title + " - " + name);
        mRef.child("genre").setValue(genre);
        mRef.child("hash").setValue(hash);
        mRef.child("name").setValue(name);
        mRef.child("playingTimes").setValue(playingTimes);
        mRef.child("skipTimes").setValue(currentSkipTimes);
        mRef.child("title").setValue(title);
        if (stateLike)
            mRef.child("liked").setValue(true);
        else
            mRef.child("liked").setValue(false);
    }
}
