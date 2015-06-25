package com.hostabee.nanodegree.activity;

import android.content.Intent;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hostabee.nanodegree.R;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * Called when the user touches the spotifyStreamerButton
     */
    public void spotifyStreamer(View view) {

        startActivity(new Intent(this, SearchForAnArtistActivity.class));
    }

    /**
     * Called when the user touches the scoresApp Button
     */
    public void scoresApp(View view) {
        makeSnackbar("This button will load my scores App");
    }

    /**
     * Called when the user touches the libraryApp Button
     */
    public void libraryApp(View view) {
        makeSnackbar("This button will load my library App");
    }

    /**
     * Called when the user touches the buildItBigger Button
     */
    public void buildItBigger(View view) {
        makeSnackbar("This button will load my build it bigger App");
    }

    /**
     * Called when the user touches the baconReader Button
     */
    public void baconReader(View view) {
        makeSnackbar("This button will load my bacon reader app");
    }

    /**
     * Called when the user touches the ownapp Button
     */
    public void capstoneMyOwnApp(View view) {
        makeSnackbar("This button will load my own app");
    }

    /**
     * Display toast with text as param
     */
    private void makeSnackbar(String text) {
        Snackbar
                .make(this.findViewById(R.id.relativeLayout), text, Snackbar.LENGTH_LONG)
                .show(); // Do not forget to show!
    }
}