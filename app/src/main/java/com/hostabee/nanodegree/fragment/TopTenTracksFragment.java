package com.hostabee.nanodegree.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostabee.nanodegree.R;
import com.hostabee.nanodegree.Utility;
import com.hostabee.nanodegree.adapter.TracksArrayAdapter;
import com.hostabee.nanodegree.asyncTask.SearchSoundTrackAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * TopTenFragment display ton ten tracks from an artist
 */
public class TopTenTracksFragment extends Fragment implements SearchSoundTrackAsyncTask.ViewI, TracksArrayAdapter.TrackAdapterOnClickHandler {

    private LinearLayoutManager mLinearLayoutManager;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArtistId = getArguments().getString(ARTIST_ID);
            mArtistName = getArguments().getString(ARTIST_NAME);
            mArtistPicture = getArguments().getString(ARTIST_PICTURE);
        }
        mTwoPane = getResources().getBoolean(R.bool.twoPane);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        if (!mTwoPane) {
                 /*Set adn init Toolbar*/
            Toolbar tolbar = (Toolbar) view.findViewById(R.id.toolbar);
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

            if (appCompatActivity != null)
                appCompatActivity.setSupportActionBar(tolbar);

            if (appCompatActivity.getSupportActionBar() != null) {
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collasping_toolbar);
            collapsingToolbarLayout.setTitle(mArtistName);
        }

        ButterKnife.bind(this, view);
        Picasso.with(getActivity()).load(mArtistPicture).into(mArtistPicitureImageView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setSelected(true);

        //If no artist id, stop process.
        if (mArtistId == null) return view;

        /**Check if state saved*/
        if (savedInstanceState != null) {
            if (savedInstanceState.getString(TRACKS_LIST_KEY) != null)
                mTracksJson = savedInstanceState.getString(TRACKS_LIST_KEY);
            List<Track> tracks = new Gson().fromJson(mTracksJson, Tracks.class).tracks;

            if (savedInstanceState.getString(TRACK_SELECTED_ID) != null)
                mTrackId = savedInstanceState.getString(TRACK_SELECTED_ID);

            mTrackArrayAdapter = new TracksArrayAdapter(getActivity(), this, tracks, mTrackId);
            mRecyclerView.setAdapter(mTrackArrayAdapter);

        } else {
            if (Utility.isNetworkAvailable(getActivity())) {
                new SearchSoundTrackAsyncTask(this).execute(mArtistId);
            } else {
                View layout = getView().findViewById(R.id.layout_top_ten);
                if (layout != null)
                    Snackbar.make(layout, "No Internet connexion", Snackbar.LENGTH_LONG).show();
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TRACKS_LIST_KEY, mTracksJson);
        outState.putString(TRACK_SELECTED_ID, mTrackId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (Callback) activity;
    }

    @Override
    public void updateView(Tracks tracks) {
        if (tracks == null || tracks.tracks.size() < 1) {

            /** Display toast with text as param */
            Snackbar.make(getView(), "The artist " + mArtistName + " has not top ten", Snackbar.LENGTH_LONG).show();
            mRecyclerView.setVisibility(View.INVISIBLE);
            //Display message no track fund
            mTacksEmptyTextView.setVisibility(View.VISIBLE);
            return;
        }
        Gson gson = new Gson();
        mTracksJson = gson.toJson(tracks);
        mTrackArrayAdapter = new TracksArrayAdapter(getActivity(), this, tracks.tracks, null);
        mRecyclerView.setAdapter(mTrackArrayAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(int position) {

        // update current postion
        mPosition = position;

        //save track id
        mTrackId = mTrackArrayAdapter.getItem(position).id;

        // Throw event to the Activity
        mCallback.onTrackClicked(position, mTracksJson, mArtistName);

        //UpdateMenu with new intent
//        mShareActionProvider.setShareIntent(shareSelectedTrack());
    }

    public void setArtistName(String name) {
        mArtistName = name;
    }

    public void updateRow(int position) {
        mPosition = position;
        mTrackId = mTrackArrayAdapter.getItem(position).id;
        mTrackArrayAdapter.setId(mTrackId);
        mLinearLayoutManager.scrollToPositionWithOffset(position,20);
    }

    public interface Callback {
        void onTrackClicked(int position, String tracksJson, String artistName);
    }

    // Share ArtistName and Selected Track
    private Intent shareSelectedTrack() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I love this song " + mTrackArrayAdapter.getItem(mPosition).name + " by " + mArtistName);
        return shareIntent;
    }

    //****************************************************
    //Fragments TAGS
    private static final String TRACK_PLAYER_TAG = "trackPlayerTag";

    private static final String TAG = TopTenTracksFragment.class.getSimpleName();
    private static final String TRACK_SELECTED_ID = "track_id";
    private static final String TRACKS_LIST_KEY = "tracksListJson";
    private static final String ARTIST_ID = "artistId";
    private static final String ARTIST_PICTURE = "artistPicture";
    private static final String ARTIST_NAME = "artistName";

    //fields members
    private String mArtistId;
    private String mArtistName;
    private String mArtistPicture;
    private String mTrackId;
    private String mTracksJson;
    private int mPosition = -1;

    private boolean mTwoPane;
    private Callback mCallback;

    private TracksArrayAdapter mTrackArrayAdapter;

    /*Views*/
    @Bind(R.id.artistPicture)
    ImageView mArtistPicitureImageView;
    @Bind(R.id.recyclerview_topten)
    RecyclerView mRecyclerView;
    @Bind(R.id.tracksEmpty)
    TextView mTacksEmptyTextView;
    private ShareActionProvider mShareActionProvider;


}
