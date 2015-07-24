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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostabee.nanodegree.R;
import com.hostabee.nanodegree.Utility;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * TrackPlayerFragment is used for playing soundtrack
 */
public class TrackPlayerFragment extends DialogFragment {

    private String START_TIME = "START_TIME";
    private String POSITION = "POSITION";
    private String IS_PLAYING = "IS_PLAYING";
    private boolean mIsPlaying = false;

    public TrackPlayerFragment() {
    }

    public static TrackPlayerFragment newInstance(int position, String tracksJson, String artistName) {
        TrackPlayerFragment fragment = new TrackPlayerFragment();
        Bundle args = new Bundle();
        args.putInt(ROW_SELECTED_POSITION, position);
        args.putString(TRACKS_LIST_KEY, tracksJson);
        args.putString(ARTIST_NAME, artistName);
        fragment.setArguments(args);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (Callback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_track_player, container, false);
        ButterKnife.bind(this, view);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTracksAsJsonString = arguments.getString(TRACKS_LIST_KEY);
            mPosition = arguments.getInt(ROW_SELECTED_POSITION);
            mArtistName = arguments.getString(ARTIST_NAME);
            mTracks = new Gson().fromJson(mTracksAsJsonString, Tracks.class).tracks;
        }

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(POSITION);
            startTime = savedInstanceState.getInt(START_TIME);
            mIsPlaying = savedInstanceState.getBoolean(IS_PLAYING);
        }
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        //Detach dialog from Activity
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
        ButterKnife.unbind(this);
        Log.v(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer.release();
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    @OnClick(R.id.backButton)
    public void onBackButton() {
        mMediaPlayer.stop();
        mIsPlaying = false;
        startTime = 0;
        if (mPosition > 0)
            mPosition--;
        else mPosition = mTracks.size() - 1;
        initMediaplayer();
        updateTrackInfo();
        // Inform TopTenActivity that track has changed
        mCallback.onTrackChange(mPosition);
    }

    @OnClick(R.id.playButton)
    public void onPlayButton() {
        if (mMediaPlayer.isPlaying()) {
            mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_18dp);
            mMediaPlayer.pause();
        } else {
            mPlayButton.setImageResource(R.drawable.ic_pause_black_18dp);
            setUpHandler();
            mMediaPlayer.start();
            mIsPlaying = true;
        }
    }

    @OnClick(R.id.forwardButton)
    public void onForwardButton() {
        mMediaPlayer.stop();
        startTime = 0;
        mIsPlaying = false;
        // if the end of the playlist return to first
        if (mPosition < mTracks.size() - 1)
            mPosition++;
        else mPosition = 0;
        initMediaplayer();
        updateTrackInfo();
        // Inform TopTenActivity that track has changed
        mCallback.onTrackChange(mPosition);
    }

    private void initMediaplayer() {
        try {

            // Only one instance of mMediaplayer
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                if (startTime > 0) {
                    mMediaPlayer.start();
                    mMediaPlayer.seekTo(startTime);
                }
            } else {
                mMediaPlayer.reset();
                mLoadingRelativeLayout.setVisibility(View.VISIBLE);
                playerButtons.setVisibility(View.INVISIBLE);
                mMediaPlayer.setDataSource(mTracks.get(mPosition).preview_url);
               mMediaPlayer.prepareAsync();
               mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                   @Override
                   public void onPrepared(MediaPlayer mp) {
                       mLoadingRelativeLayout.setVisibility(View.GONE);
                       playerButtons.setVisibility(View.VISIBLE);
                   }
               });
               // might take long! (for buffering, etc)
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTrackInfo() {

        //Local variable, track
        Track track = mTracks.get(mPosition);

        //Add Album picture
        Picasso.with(this.getActivity()).load(track.album.images.get(0).url).into(mAlbumArtWorkImageView);
        //Album name
        mAlbumNameTextView.setText(track.album.name);
        //ArtistName
        mArtistNameTextView.setText(mArtistName);

        //Play button setIcon
        if (!mMediaPlayer.isPlaying())
            mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_18dp);
        else
            mPlayButton.setImageResource(R.drawable.ic_pause_black_18dp);

        // Set track name
        mTrackNameTextView.setText(track.name);

        //Set track duration
        mTrackdurationTextView.setText(Utility.getDurationFromMsToMinSec(track.duration_ms));

        if (mMediaPlayer != null) {
            mTimePositionTextView.setText(Utility.getDurationFromMsToMinSec((long) startTime));

            mSeeBar.setMax(mMediaPlayer.getDuration());

            if (mMediaPlayer.isPlaying()) {
                mPlayButton.setImageResource(R.drawable.ic_pause_black_18dp);
            } else {
                mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_18dp);
            }
            setUpHandler();
        } else {
            mTimePositionTextView.setText("0:00");
            mSeeBar.setProgress(0);
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private Runnable updateTrackTime = new Runnable() {
        public void run() {
            startTime = mMediaPlayer.getCurrentPosition();
            mTimePositionTextView.setText(Utility.getDurationFromMsToMinSec((long) startTime));
            mSeeBar.setProgress(startTime);
            mHandler.postDelayed(this, 500);
        }
    };

    private void setUpHandler() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(updateTrackTime, 500);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(START_TIME, startTime);
        outState.putInt(POSITION, mPosition);
        if (mMediaPlayer != null)
            outState.putBoolean(IS_PLAYING, mMediaPlayer.isPlaying());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        initMediaplayer();
        updateTrackInfo();
    }

    @Override
    public void onDetach() {
        Log.v(TAG, "OnDetach");
        super.onDetach();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        Log.v(TAG, "onViewStateRestored");
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        //if (mMediaPlayer != null) mMediaPlayer.pause();
        mHandler.removeCallbacksAndMessages(null);
        Log.v(TAG, "onStop");
    }

    public interface Callback {
        void onTrackChange(int position);
    }

    // Fields
    private static final String TAG = TrackPlayerFragment.class.getName();

    private final Handler mHandler = new Handler();

    @Bind(R.id.playButton)
    ImageButton mPlayButton;
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
    @Bind(R.id.loadingPanel)
    ProgressBar mLoadingRelativeLayout;
    @Bind(R.id.playerButtons)
    LinearLayout playerButtons;

    private static final String TRACKS_LIST_KEY = "tracksListJson";
    private static final String ROW_SELECTED_POSITION = "position";
    private static final String ARTIST_NAME = "artistName";

    private String mTracksAsJsonString;
    private String mArtistName;
    private int mPosition;
    private int startTime = 0;

    private List<Track> mTracks;

    private static MediaPlayer mMediaPlayer;

    private Callback mCallback;
}
