package com.hostabee.nanodegree.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerFragment extends DialogFragment {

    private Callback fragmentCallback;

    @Bind(R.id.playButton)
    ImageButton mPlayButton;
    @Bind(R.id.backButton)
    ImageButton mBackButton;
    @Bind(R.id.forwardButton)
    ImageButton mForwardButton;
    @Bind(R.id.artistNameTextView)
    TextView mArtistNameTextView;
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

    private static final String TRACKS_LIST_KEY = "tracksListJson";
    private static final String ROW_SELECTED_POSITION = "position";
    private static final String ARTIST_NAME = "artistName";
    private String mTracksAsJsonString;
    private int mPosition;
    private String mArtistName;
    private List<Track> mTracks;

    MediaPlayer mMediaPlayer;


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
            mArtistNameTextView.setText(mArtistName);
            mAlbumNameTextView.setText(mTracks.get(mPosition).album.name);
            updateTrackInfo();
        }


        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        if (mPosition > 0)
            mPosition--;
        else mPosition = mTracks.size() - 1;
        Log.v("Position", "Position" + mPosition + "Tracks : " + mTracks.size());
        updateTrackInfo();
        playTrack();
    }

    @OnClick(R.id.playButton)
    public void onPlayButton() {
        playTrack();
    }

    @OnClick(R.id.forwardButton)
    public void onForwardButton() {
        if (mPosition < mTracks.size() - 1)
            mPosition++;
        else mPosition = 0;

        updateTrackInfo();
        playTrack();
    }

    void playTrack() {
        try {
            if (mMediaPlayer != null) mMediaPlayer.release();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(mTracks.get(mPosition).preview_url);
            mMediaPlayer.prepare(); // might take long! (for buffering, etc)
//            mTrackdurationTextView.setText((int) mTracks.get(mPosition).duration_ms);
            //           mTimePositionTextView.setText(mediaPlayer.getCurrentPosition());

            mSeeBar.setMax(mMediaPlayer.getDuration());
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updateTrackInfo() {
        mTrackNameTextView.setText(mTracks.get(mPosition).name);
        Picasso.with(this.getActivity()).load(mTracks.get(mPosition).album.images.get(0).url).into(mAlbumArtWorkImageView);
        mTimePositionTextView.setText("0:00");
        mTrackdurationTextView.setText("3:28");
    }

    public interface Callback {
        void onPlay();

        void onForward();

        void onBack();
    }

}
