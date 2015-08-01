package com.hostabee.nanodegree.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


/*
MediaPlayerService is used to play Music
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    private final IBinder musicBind = new MusicBinder();

    private static final String TAG = "MediaPlayerService";

    private static final String COM_HOSTABEE_BROADCAST_ON_PREPARED = "com.hostabee.broadcast.onPrepared";

    private MediaPlayer mMediaPlayer = null;

    private List<Track> tracks;

    private boolean pause = false;

    public MediaPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public void onCreate() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void setList(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        return false;
    }

    public boolean isPause() {
        return pause;
    }

    public void preloadTrack(int position) {
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(tracks.get(position).preview_url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "preloadTrack");
        }
        mMediaPlayer.prepareAsync();
    }

    public void playSong() {
        if (pause) {
            mMediaPlayer.start();
            pause = false;
            return;
        }
        mMediaPlayer.start();
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getCurrentPosition();
        else
            return -1;
    }

    public void pauseSong() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            pause = true;
        }
    }

    public void stopSong() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public int getDuration() {
        if (mMediaPlayer != null)
            return mMediaPlayer.getDuration();
        else
            return -1;
    }

    /**
     * Called when Track is ready, send info to Activity
     */
    @Override
    public void onPrepared(MediaPlayer player) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(COM_HOSTABEE_BROADCAST_ON_PREPARED);
        sendBroadcast(broadcastIntent);
    }

    public class MusicBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}
