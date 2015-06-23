package com.hostabee.nanodegree.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hostabee.nanodegree.R;
import com.hostabee.nanodegree.adapter.TracksArrayAdapter;
import com.hostabee.nanodegree.asyncTask.SearchSoundTrackAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


@SuppressWarnings({"ALL", "unused"})
public class TopTenTracksActivity extends AppCompatActivity implements SearchSoundTrackAsyncTask.ViewI {


    private TracksArrayAdapter mTrackArrayAdapter;
    private String mTracksJson;
    private String mArtistId;
    private String mArtistName;
    private String mArtistPicture;
    private static final String TRACKS_LIST_KEY = "tracksListJson";
    private static final String ARTIST_ID = "artistId";
    private static final String ARTIST_PICTURE = "artistPicture";
    private static final String ARTIST_NAME = "artistName";

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*init views*/

        setContentView(R.layout.activity_top_ten_tracks);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collasping_toolbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_topten);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*Retrieve params from Intent*/
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(ARTIST_ID)) {

            // If picture is null init late spotify picture
            if (intent.getStringExtra(ARTIST_PICTURE) != null) {
                ImageView imageView = (ImageView) findViewById(R.id.artistPicture);
                mArtistPicture = intent.getStringExtra(ARTIST_PICTURE);
                Picasso.with(this).load(mArtistPicture).into(imageView);
            }

            mArtistId = intent.getStringExtra(ARTIST_ID);
            mArtistName = intent.getStringExtra(ARTIST_NAME);
            collapsingToolbarLayout.setTitle(mArtistName);
        }

        /**Check if state saved*/
        if (savedInstanceState != null && savedInstanceState.getString(TRACKS_LIST_KEY) != null) {
            mTracksJson = savedInstanceState.getString(TRACKS_LIST_KEY);
            List<Track> tracks = new Gson().fromJson(mTracksJson, Tracks.class).tracks;
            mTrackArrayAdapter = new TracksArrayAdapter(getBaseContext(), tracks);
            mRecyclerView.setAdapter(mTrackArrayAdapter);
        } else {
            new SearchSoundTrackAsyncTask(this).execute(mArtistId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString(TRACKS_LIST_KEY, mTracksJson);
        super.onSaveInstanceState(bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_ten_tracks, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateView(Tracks tracks) {
        if (tracks == null || tracks.tracks.size() < 1) {
            /** Display toast with text as param */
            //Snackbar.make(findViewById(R.id.layout_top_ten), "The artist " + mArtistName + " has not top ten", Snackbar.LENGTH_LONG).show();
            mRecyclerView.setVisibility(View.INVISIBLE);
            //Display message no track fund
            findViewById(R.id.tracksEmpty).setVisibility(View.VISIBLE);
            return;
        }
        Gson gson = new Gson();
        mTracksJson = gson.toJson(tracks);
        mTrackArrayAdapter = new TracksArrayAdapter(getBaseContext(), tracks.tracks);
        mRecyclerView.setAdapter(mTrackArrayAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
