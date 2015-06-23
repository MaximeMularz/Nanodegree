package com.hostabee.nanodegree.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostabee.nanodegree.R;
import com.hostabee.nanodegree.Utility;
import com.hostabee.nanodegree.adapter.ArtistArrayAdapter;
import com.hostabee.nanodegree.asyncTask.SearchArtistAsyncTask;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;

public class SearchForAnArtistActivity extends AppCompatActivity implements SearchArtistAsyncTask.ViewI {

    /*static members*/
    private final String TAG = "SearchF";
    private static final String ARTIST_ID = "artistId";
    private static final String ARTIST_NAME = "artistName";
    private static final String ARTIST_PICTURE = "artistPicture";
    private static final String ARTIST_LIST_KEY = "artistListJson";
    private static final String SELECTED_KEY = "selected_position";

    /*Data*/
    private ArtistArrayAdapter mArtistArrayAdapter;
    private String mArtistName;
    private String artitsListJson;

    /*Views*/
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private EditText mSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_for_an_artist);

        /*Set adn init Toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.listview_spotify);

        View emptyView = findViewById(R.id.listview_artist_empty);
        mListView.setEmptyView(emptyView);

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
                mArtistArrayAdapter = new ArtistArrayAdapter(getBaseContext(), new Gson().fromJson(artitsListJson, Artists.class).artists);
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
    }

    private void initListViewListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Artist artist = mArtistArrayAdapter.getItem(position);

                Intent intent = new Intent(SearchForAnArtistActivity.this, TopTenTracksActivity.class);
                intent.putExtra(ARTIST_ID, artist.id);
                intent.putExtra(ARTIST_NAME, artist.name);

                if (artist.images != null && artist.images.size() > 0) {
                    // add artist picture url, the last one, best resolution
                    intent.putExtra(ARTIST_PICTURE, artist.images.get(0).url);
                }

                mPosition = position;

                startActivity(intent);
            }
        });
    }

    private void initSearchEditTextView() {
        mSearchView = (EditText) findViewById(R.id.searchView);

        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mArtistName = v.getText().toString();

                    if (!mArtistName.equals(""))
                        if (Utility.isNetworkAvailable(SearchForAnArtistActivity.this)) {
                            new SearchArtistAsyncTask(SearchForAnArtistActivity.this).execute(mArtistName);
                        } else {
                            Snackbar.make(SearchForAnArtistActivity.this.findViewById(R.id.mainLayout), "No Internet connexion", Snackbar.LENGTH_LONG).show();
                        }
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(ARTIST_NAME, mArtistName);
        savedInstanceState.putString(ARTIST_LIST_KEY, artitsListJson);

        if (mPosition != ListView.INVALID_POSITION) {
            savedInstanceState.putInt(SELECTED_KEY, mPosition);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spotify_streamer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void updateView(Artists artists) {

        if (artists == null || artists.artists.size() < 1) {
            /** Display toast with text as param */
            Snackbar.make(findViewById(R.id.mainLayout), "The artist " + mArtistName + " is not found (asks to refine search)", Snackbar.LENGTH_LONG).show();
            mListView.setAdapter(null);
            artitsListJson = null;
            mArtistArrayAdapter = null;
            return;
        }

        Gson gson = new Gson();
        artitsListJson = gson.toJson(artists);
        mArtistArrayAdapter = new ArtistArrayAdapter(getBaseContext(), artists.artists);
        mListView.setAdapter(mArtistArrayAdapter);
    }
}


