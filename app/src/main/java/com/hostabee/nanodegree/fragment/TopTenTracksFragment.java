package com.hostabee.nanodegree.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostabee.nanodegree.R;
import com.hostabee.nanodegree.Utility;
import com.hostabee.nanodegree.activity.TrackPlayerActivity;
import com.hostabee.nanodegree.adapter.TracksArrayAdapter;
import com.hostabee.nanodegree.asyncTask.SearchSoundTrackAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TopTenTracksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopTenTracksFragment extends Fragment implements SearchSoundTrackAsyncTask.ViewI, TracksArrayAdapter.TrackAdapterOnClickHandler {

    private static final String ROW_SELECTED_POSITION = "position";
    private TracksArrayAdapter mTrackArrayAdapter;
    private String mTracksJson;

    private static final String TRACKS_LIST_KEY = "tracksListJson";
    private static final String ARTIST_ID = "artistId";
    private static final String ARTIST_PICTURE = "artistPicture";
    private static final String ARTIST_NAME = "artistName";

    //defaiult data
    private String mArtistId;
    private String mArtistName;
    private String mArtistPicture;

    private Object mPosition;

    private Callback mCallback;

    /*Views*/
    @Bind(R.id.artistPicture)
    ImageView mArtistPicitureImageView;

    @Bind(R.id.recyclerview_topten)
    RecyclerView mRecyclerView;

    @Bind(R.id.tracksEmpty)
    TextView mTacksEmptyTextView;

    // TODO: Rename and change types and number of parameters
    public static TopTenTracksFragment newInstance(String artistId, String artistName, String artistPictureUrl) {
        TopTenTracksFragment fragment = new TopTenTracksFragment();
        Bundle args = new Bundle();
        args.putString(ARTIST_ID, artistId);
        args.putString(ARTIST_NAME, artistName);
        args.putString(ARTIST_PICTURE, artistPictureUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public TopTenTracksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArtistId = getArguments().getString(ARTIST_ID);
            mArtistName = getArguments().getString(ARTIST_NAME);
            mArtistPicture = getArguments().getString(ARTIST_PICTURE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);
        ButterKnife.bind(this, view);
        Picasso.with(getActivity()).load(mArtistPicture).into(mArtistPicitureImageView);

        //mCollapsingToolbarLayout.setTitle(mArtistName);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        if (mArtistId == null) return view;

        /**Check if state saved*/
        if (savedInstanceState != null && savedInstanceState.getString(TRACKS_LIST_KEY) != null) {
            mTracksJson = savedInstanceState.getString(TRACKS_LIST_KEY);
            List<Track> tracks = new Gson().fromJson(mTracksJson, Tracks.class).tracks;
            mTrackArrayAdapter = new TracksArrayAdapter(getActivity(), this, tracks);
            mRecyclerView.setAdapter(mTrackArrayAdapter);
        } else {
            if (Utility.isNetworkAvailable(getActivity())) {
                new SearchSoundTrackAsyncTask(this).execute(mArtistId);
            } else {
                Snackbar.make(getView().findViewById(R.id.layout_top_ten), "No Internet connexion", Snackbar.LENGTH_LONG).show();
            }

        }

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TRACKS_LIST_KEY, mTracksJson);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (Callback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void updateView(Tracks tracks) {
        if (tracks == null || tracks.tracks.size() < 1) {

            /** Display toast with text as param */
            //Snackbar.make(findViewById(R.id.layout_top_ten), "The artist " + mArtistName + " has not top ten", Snackbar.LENGTH_LONG).show();
            mRecyclerView.setVisibility(View.INVISIBLE);
            //Display message no track fund
            mTacksEmptyTextView.setVisibility(View.VISIBLE);
            return;
        }
        Gson gson = new Gson();
        mTracksJson = gson.toJson(tracks);
        mTrackArrayAdapter = new TracksArrayAdapter(getActivity(), this, tracks.tracks);
        mRecyclerView.setAdapter(mTrackArrayAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(int position) {
        mCallback.onTrackClicked(position, mTracksJson, mArtistName);
    }

    public interface Callback {
        void onTrackClicked(int position, String tracksJson, String artistName);
    }
}
