package com.hostabee.nanodegree.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.hostabee.nanodegree.R;
import com.hostabee.nanodegree.fragment.TopTenTracksFragment;
import com.hostabee.nanodegree.fragment.TrackPlayerFragment;


@SuppressWarnings({"ALL", "unused"})
public class TopTenTracksActivity extends AppCompatActivity implements TopTenTracksFragment.Callback{

    private static final String TRACKS_LIST_KEY = "tracksListJson";
    private static final String ARTIST_ID = "artistId";
    private static final String ARTIST_PICTURE = "artistPicture";
    private static final String ARTIST_NAME = "artistName";
    private static final String ROW_SELECTED_POSITION = "position";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten_tracks_one_pane);


        /*Retrieve params from Intent*/
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(ARTIST_ID)) {

            String artistPicture = intent.getStringExtra(ARTIST_PICTURE);
            String artistId = intent.getStringExtra(ARTIST_ID);
            String artistName = intent.getStringExtra(ARTIST_NAME);

            TopTenTracksFragment fragment = TopTenTracksFragment.newInstance(artistId, artistName, artistPicture);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment, fragment)
                        .commit();
            }
        }


    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
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
    public void onTrackClicked(int position, String tracksJson, String artistName) {
        Intent intent = new Intent(this, TrackPlayerActivity.class);
        intent.putExtra(ROW_SELECTED_POSITION, position);
        intent.putExtra(TRACKS_LIST_KEY, tracksJson);
        intent.putExtra(ARTIST_NAME, artistName);
       // startActivity(intent);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        TrackPlayerFragment fragment = TrackPlayerFragment.newInstance(position, tracksJson, artistName);
        fragment.show(getSupportFragmentManager(), "dialog");


    }
}
