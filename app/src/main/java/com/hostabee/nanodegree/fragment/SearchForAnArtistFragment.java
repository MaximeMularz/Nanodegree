package com.hostabee.nanodegree.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostabee.nanodegree.R;
import com.hostabee.nanodegree.Utility;
import com.hostabee.nanodegree.adapter.ArtistArrayAdapter;
import com.hostabee.nanodegree.asyncTask.SearchArtistAsyncTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;

public class SearchForAnArtistFragment extends Fragment implements SearchArtistAsyncTask.ViewI {

    private static final String ARTIST_NAME = "artistName";
    private static final String ARTIST_LIST_KEY = "artistListJson";
    private static final String SELECTED_KEY = "selected_position";

    /*Data*/
    private ArtistArrayAdapter mArtistArrayAdapter;
    private String mArtistName;
    private String artitsListJson;

    private Callback mCallback;

    /*Views*/
    @SuppressWarnings("WeakerAccess")
    @Bind(R.id.listview_spotify)
    ListView mListView;

    @SuppressWarnings("WeakerAccess")
    @Bind(R.id.searchView)
    SearchView mSearchView;

    @SuppressWarnings("WeakerAccess")
    @Bind(R.id.listview_artist_empty)
    TextView mEmptyView;

    @SuppressWarnings("WeakerAccess")
    @Bind(R.id.mainLayout)
    LinearLayout mMainLayoutLinearLayout;

    private int mPosition = ListView.INVALID_POSITION;

    public SearchForAnArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean mTwoPane = getResources().getBoolean(R.bool.twoPane);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_for_an_artist, container, false);

        ButterKnife.bind(this, view);
        mListView.setEmptyView(mEmptyView);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mArtistName = savedInstanceState.getString(ARTIST_NAME);
            artitsListJson = savedInstanceState.getString(ARTIST_LIST_KEY);

            if (savedInstanceState.containsKey(SELECTED_KEY)) {
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }

            //Check results
            if (mArtistName != null && artitsListJson != null) {
                mArtistArrayAdapter = new ArtistArrayAdapter(getActivity(), new Gson().fromJson(artitsListJson, Artists.class).artists);
                mListView.setAdapter(mArtistArrayAdapter);
                if (mPosition != ListView.INVALID_POSITION) {
                    // If we don't need to restart the loader, and there's a desired position to restore
                    // to, do so now.
                    mListView.smoothScrollToPosition(mPosition);
                }
            }
        }


        initSearchEditTextView();

        initListViewListener();


        return view;
    }


    /**/
    private void initListViewListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Artist artist = mArtistArrayAdapter.getItem(position);

                mCallback.onArtistClicked(artist);

                mPosition = position;

            }
        });
    }

    /**/
    private void initSearchEditTextView() {


        mSearchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        mArtistName = query;

                        if (!mArtistName.equals(""))
                            if (Utility.isNetworkAvailable(getActivity())) {
                                new SearchArtistAsyncTask(SearchForAnArtistFragment.this).execute(mArtistName);
                            } else {
                                Snackbar.make(mMainLayoutLinearLayout, "No Internet connexion", Snackbar.LENGTH_LONG).show();
                            }
                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
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
    public void onSaveInstanceState(Bundle outState) {
        // Save the user's current game state
        outState.putString(ARTIST_NAME, mArtistName);
        outState.putString(ARTIST_LIST_KEY, artitsListJson);

        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }


    @Override
    public void updateView(Artists artists) {

        if (artists == null || artists.artists.size() < 1) {
            /** Display toast with text as param */
            Snackbar.make(mMainLayoutLinearLayout, "The artist " + mArtistName + " is not found (asks to refine search)", Snackbar.LENGTH_LONG).show();
            mListView.setAdapter(null);
            artitsListJson = null;
            mArtistArrayAdapter = null;
            return;
        }

        Gson gson = new Gson();
        artitsListJson = gson.toJson(artists);
        mArtistArrayAdapter = new ArtistArrayAdapter(getActivity(), artists.artists);
        mListView.setAdapter(mArtistArrayAdapter);
    }

    public interface Callback {
        void onArtistClicked(Artist artist);
    }
}
