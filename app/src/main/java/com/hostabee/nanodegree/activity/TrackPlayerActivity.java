package com.hostabee.nanodegree.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hostabee.nanodegree.R;
import com.hostabee.nanodegree.fragment.TrackPlayerFragment;

public class TrackPlayerActivity extends AppCompatActivity {

    private static final String TRACKS_LIST_KEY = "tracksListJson";
    private static final String ROW_SELECTED_POSITION = "position";
    private static final String TRACK_PLAYER_TAG = "trackPlayerTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_player);

        Log.v("TrackPlayerActivity", "TrackPlayerActivity");

        TrackPlayerFragment fragment = new TrackPlayerFragment();
        fragment.setArguments(getIntent().getExtras());
        fragment.show(getSupportFragmentManager(), TRACK_PLAYER_TAG);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_player, menu);
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
}
