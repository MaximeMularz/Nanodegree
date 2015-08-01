package com.hostabee.nanodegree.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostabee.nanodegree.R;
import com.hostabee.nanodegree.Utility;
import com.hostabee.nanodegree.service.MediaPlayerService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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

    //Keys
    private static final String TRACKS_LIST_KEY = "tracksListJson";
    private static final String ROW_SELECTED_POSITION = "position";
    private static final String ARTIST_NAME = "artistName";
    private String START_TIME = "START_TIME";
    private String POSITION = "POSITION";

    //Data members
    private String mTracksAsJsonString;
    private String mArtistName;
    private int mPosition;
    private int startTime = 0;
    private List<Track> mTracks;
    private IntentFilter mIntentFilter;

    // Activity interface
    private Callback mCallback;

    //Service Mediaplayer and parameters
    private MediaPlayerService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    // others
    private static final String TAG = TrackPlayerFragment.class.getName();
    private static final String COM_HOSTABEE_BROADCAST_ON_PREPARED = "com.hostabee.broadcast.onPrepared";

    // handler for Runnable
    private final Handler mHandler = new Handler();

    //Views
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
        View view = inflater.inflate(R.layout.fragment_track_player, container, false);
        ButterKnife.bind(this, view);

        playerButtons.setVisibility(View.VISIBLE);

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getApplicationContext().unbindService(musicConnection);
        getActivity().getApplicationContext().stopService(playIntent);
    }

    @OnClick(R.id.backButton)
    public void onBackButton() {
        musicSrv.stopSong();
        if (mPosition > 0)
            mPosition--;
        else mPosition = mTracks.size() - 1;
        updateTrackInfo();
        musicSrv.preloadTrack(mPosition);
        // Inform TopTenActivity that track has changed
        mCallback.onTrackChange(mPosition);

    }

    @OnClick(R.id.playButton)
    public void onPlayButton() {
        if (musicSrv.isPlaying()) {
            mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_18dp);
            musicSrv.pauseSong();
        } else {
            mPlayButton.setImageResource(R.drawable.ic_pause_black_18dp);
            setUpHandler();
            musicSrv.playSong();
        }
    }

    @OnClick(R.id.forwardButton)
    public void onForwardButton() {
        musicSrv.stopSong();
        // if the end of the playlist return to first
        if (mPosition < mTracks.size() - 1)
            mPosition++;
        else mPosition = 0;
        updateTrackInfo();
        musicSrv.preloadTrack(mPosition);
        // Inform TopTenActivity that track has changed
        mCallback.onTrackChange(mPosition);
    }


    private void updateTrackInfo() {

        hideMediaController();

        mHandler.removeCallbacksAndMessages(null);

        //Local variable, track
        Track track = mTracks.get(mPosition);

        //Add Album picture
        Picasso.with(this.getActivity()).load(track.album.images.get(0).url).into(mAlbumArtWorkImageView);
        //Album name
        mAlbumNameTextView.setText(track.album.name);
        //ArtistName
        mArtistNameTextView.setText(mArtistName);

        // Set track name
        mTrackNameTextView.setText(track.name);

        //Set track duration
        mTrackdurationTextView.setText(Utility.getDurationFromMsToMinSec(track.duration_ms));

        if (musicSrv != null && musicBound) {

            if (musicSrv.getCurrentPosition() > 0) {
                mTimePositionTextView.setText(Utility.getDurationFromMsToMinSec(musicSrv.getCurrentPosition()));
                displayMediaController();
                mSeeBar.setProgress(musicSrv.getCurrentPosition());
            }

            if (musicSrv.isPlaying()) {
                mPlayButton.setImageResource(R.drawable.ic_pause_black_18dp);
            }

            if (musicSrv.isPause()) {
                mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_18dp);
            }

        } else {
            //upload Song
            //musicSrv.preloadTrack(mPosition);
            playerButtons.setVisibility(View.INVISIBLE);
            mTimePositionTextView.setText("0:00");
            mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_18dp);
            startTime = 0;
        }
        setUpHandler();
    }

    private Runnable updateTrackTime = new Runnable() {
        public void run() {
            if (musicSrv != null)
                startTime = musicSrv.getCurrentPosition();
            else
                startTime = 0;
            //Log.v(TAG, "startTime = " + startTime);
            mTimePositionTextView.setText(Utility.getDurationFromMsToMinSec(startTime));
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
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (playIntent == null) {
            playIntent = new Intent(getActivity().getApplicationContext(), MediaPlayerService.class);
            getActivity().startService(playIntent);
            getActivity().getApplicationContext().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(COM_HOSTABEE_BROADCAST_ON_PREPARED);
    }


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.MusicBinder binder = (MediaPlayerService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList((ArrayList<Track>) mTracks);
            musicBound = true;

            musicSrv.preloadTrack(mPosition);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        updateTrackInfo();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler.removeCallbacksAndMessages(null);
    }


    public interface Callback {
        void onTrackChange(int position);
    }

    // Catch Service events, track ready to play
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(COM_HOSTABEE_BROADCAST_ON_PREPARED)) {
                displayMediaController();
            }
        }
    };

    private void displayMediaController() {
        mLoadingRelativeLayout.setVisibility(View.GONE);
        playerButtons.setVisibility(View.VISIBLE);
        mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_18dp);
        mSeeBar.setMax(musicSrv.getDuration());
    }

    private void hideMediaController() {
        mLoadingRelativeLayout.setVisibility(View.VISIBLE);
        playerButtons.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }
}