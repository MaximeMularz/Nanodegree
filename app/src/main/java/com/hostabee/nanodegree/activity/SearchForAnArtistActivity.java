package com.hostabee.nanodegree.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hostabee.nanodegree.R;
import com.hostabee.nanodegree.asyncTask.SearchSoundTrackAsyncTask;
import com.hostabee.nanodegree.fragment.SearchForAnArtistFragment;
import com.hostabee.nanodegree.fragment.TopTenTracksFragment;
import com.hostabee.nanodegree.fragment.TrackPlayerFragment;

import kaaes.spotify.webapi.android.models.Artist;

public class SearchForAnArtistActivity extends AppCompatActivity implements SearchForAnArtistFragment.Callback, TopTenTracksFragment.Callback, TrackPlayerFragment.Callback {

    //Keys
    private static final String ARTIST_ID = "artistId";
    private static final String ARTIST_PICTURE = "artistPicture";
    private static final String ARTIST_NAME = "artistName";

    //Fragment tags
    private static final String SEARCH_FOR_AN_ARTIST_TAG = "SEARCH_FOR_AN_ARTIST_TAG";
    private static final String TOP_TEN_TRACKS_TAG = "TOP_TEN_TRACKS_TAG";
    private static final String TRACK_PLAYER_TAG = "TRACK_PLAYER_TAG";

    //members
    private boolean mTwoPane;
    private String mArtistId;
    private String mArtistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTwoPane = getResources().getBoolean(R.bool.twoPane);

        setContentView(R.layout.activity_search_for_an_artist);

        if (savedInstanceState == null) {
            SearchForAnArtistFragment fragment = new SearchForAnArtistFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_artist_container, fragment, SEARCH_FOR_AN_ARTIST_TAG)
                    .commit();
        }

           /*Set adn init Toolbar*/
        Toolbar mToolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(mToolbar);
        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }


        if (mTwoPane) {

            if (savedInstanceState != null) {
                TopTenTracksFragment fragment1 = (TopTenTracksFragment) getSupportFragmentManager().findFragmentByTag(TOP_TEN_TRACKS_TAG);
                mArtistId = savedInstanceState.getString(ARTIST_ID);
                mArtistName = savedInstanceState.getString(ARTIST_NAME);
                fragment1.setArtistId(mArtistId);
                if (getSupportActionBar() != null)
                    getSupportActionBar().setSubtitle(mArtistName);
            } else {
                TopTenTracksFragment topTenTracksFragment = new TopTenTracksFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.top_ten_track_container, topTenTracksFragment, TOP_TEN_TRACKS_TAG)
                        .commit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(ARTIST_ID, mArtistId);
        savedInstanceState.putString(ARTIST_NAME, mArtistName);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (mTwoPane) {
            getMenuInflater().inflate(R.menu.menu_top_ten_tracks, menu);
        } else {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_spotify_streamer, menu);
        }
        return !mTwoPane;
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
    public void onArtistClicked(Artist artist) {

        if (mTwoPane) {
            FragmentManager fm = getSupportFragmentManager();
            TopTenTracksFragment topTenTracksFragment = (TopTenTracksFragment) fm.findFragmentByTag(TOP_TEN_TRACKS_TAG);
            new SearchSoundTrackAsyncTask(topTenTracksFragment).execute(artist.id);
            topTenTracksFragment.setArtistName(artist.name);
            mArtistId = artist.id;
            mArtistName = artist.name;
            if (getSupportActionBar() != null)
                getSupportActionBar().setSubtitle(artist.name);
        } else {
            Intent intent = new Intent(this, TopTenTracksActivity.class);
            intent.putExtra(ARTIST_ID, artist.id);
            intent.putExtra(ARTIST_NAME, artist.name);

            if (artist.images != null && artist.images.size() > 0) {
                // add artist picture url, the last one, best resolution
                intent.putExtra(ARTIST_PICTURE, artist.images.get(0).url);
            }
            startActivity(intent);
        }
    }

    @Override
    public void onTrackClicked(int position, String tracksJson, String artistName) {
        TrackPlayerFragment fragment = TrackPlayerFragment.newInstance(position, tracksJson, artistName);
        fragment.show(getSupportFragmentManager(), TRACK_PLAYER_TAG);
    }

    //update
    @Override
    public void onTrackChange(int position) {
        TopTenTracksFragment fragment = (TopTenTracksFragment) getSupportFragmentManager().findFragmentByTag(TOP_TEN_TRACKS_TAG);
        fragment.updateRow(position);
    }
}


