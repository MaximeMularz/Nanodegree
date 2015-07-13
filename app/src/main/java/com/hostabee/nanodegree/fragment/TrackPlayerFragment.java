package com.hostabee.nanodegree.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostabee.nanodegree.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerFragment extends DialogFragment implements MediaPlayer.OnSeekCompleteListener {

    private Callback fragmentCallback;

    private Handler mHandler = new Handler();

    @Bind(R.id.playButton)
    ImageButton mPlayButton;
    @Bind(R.id.backButton)
    ImageButton mBackButton;
    @Bind(R.id.forwardButton)
    ImageButton mForwardButton;
    @Bind(R.id.albumNameTextView)
    TextView mAlbumNameTextView;
    @Bind(R.id.AlbumArtWorkImageView)
    ImageView mAlbumArtWorkImageView;
    @Bind(R.id.trackNameTextView)
    TextView mTrackNameTextView;
    @Bind(R.id.seekBar)
    SeekBar mSeeBar;
    @Bind(R.id.timePositionTextView)
    TextView mTimePositionTextView;
    @Bind(R.id.trackdurationTextView)
    TextView mTrackdurationTextView;
    @Bind(R.id.artistNameTextView)
    TextView mArtistNameTextView;


    private static final String TRACKS_LIST_KEY = "tracksListJson";
    private static final String ROW_SELECTED_POSITION = "position";
    private static final String ARTIST_NAME = "artistName";
    private String mTracksAsJsonString;
    private int mPosition;
    private String mArtistName;
    private List<Track> mTracks;

    private MediaPlayer mMediaPlayer;


    public TrackPlayerFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

    }

    public static TrackPlayerFragment newInstance(int position, String tracksJson, String artistName) {
        TrackPlayerFragment fragment = new TrackPlayerFragment();
        Bundle args = new Bundle();
        args.putInt(ROW_SELECTED_POSITION, position);
        args.putString(TRACKS_LIST_KEY, tracksJson);
        args.putString(ARTIST_NAME, artistName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_player, container, false);
        ButterKnife.bind(this, view);

        Bundle arguments = getArguments();

        if (arguments != null) {
            mTracksAsJsonString = arguments.getString(TRACKS_LIST_KEY);
            mPosition = arguments.getInt(ROW_SELECTED_POSITION);
            mArtistName = arguments.getString(ARTIST_NAME);
            mTracks = new Gson().fromJson(mTracksAsJsonString, Tracks.class).tracks;
            mAlbumNameTextView.setText(mTracks.get(mPosition).album.name);
            mArtistNameTextView.setText(mArtistName);
            updateTrackInfo();
            initMediaplayer();
        }


        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer.release();
        super.onDestroy();
    }


    @OnClick(R.id.backButton)
    public void onBackButton() {
        mMediaPlayer.release();

        if (mPosition > 0)
            mPosition--;
        else mPosition = mTracks.size() - 1;

        updateTrackInfo();
        initMediaplayer();
    }

    @OnClick(R.id.playButton)
    public void onPlayButton() {

        if (mMediaPlayer.isPlaying()) {
            mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_18dp);
            mMediaPlayer.pause();
        } else {
            mPlayButton.setImageResource(R.drawable.ic_pause_black_18dp);
            mMediaPlayer.start();
        }
    }

    @OnClick(R.id.forwardButton)
    public void onForwardButton() {
        mMediaPlayer.release();
        if (mPosition < mTracks.size() - 1)
            mPosition++;
        else mPosition = 0;
        updateTrackInfo();
        initMediaplayer();
    }


    private void initMediaplayer() {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(mTracks.get(mPosition).preview_url);
            mMediaPlayer.prepare(); // might take long! (for buffering, etc)

            mMediaPlayer.setOnSeekCompleteListener(this);


            mSeeBar.setMax(mMediaPlayer.getDuration());
            finalTime = mMediaPlayer.getDuration();
            startTime = mMediaPlayer.getCurrentPosition();
            setUpHandler();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTrackInfo() {
        mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_18dp);
        mTrackNameTextView.setText(mTracks.get(mPosition).name);
        Picasso.with(this.getActivity()).load(mTracks.get(mPosition).album.images.get(0).url).into(mAlbumArtWorkImageView);
        mTimePositionTextView.setText("0:00");
        mTrackdurationTextView.setText(String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mTracks.get(mPosition).duration_ms),
                TimeUnit.MILLISECONDS.toSeconds(mTracks.get(mPosition).duration_ms) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mTracks.get(mPosition).duration_ms))));


    }


    private Runnable updateTrackTime = new Runnable() {
        public void run() {
            startTime = mMediaPlayer.getCurrentPosition();
            mTimePositionTextView.setText(String.format("%d:%02d",

                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );
            mSeeBar.setProgress((int) startTime);
            mHandler.postDelayed(this, 500);
            Log.v("updateTrackTime", "updateTrackTime() :" + mMediaPlayer.getDuration());
        }
    };


    private void setUpHandler(){
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(updateTrackTime, 500);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler.removeCallbacksAndMessages(null);
    }

    private double startTime = 0;
    private double finalTime = 0;

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        onForwardButton();
    }

    public interface Callback {
        void onPlay();

        void onForward();

        void onBack();

    }

}
