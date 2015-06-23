package com.hostabee.nanodegree.asyncTask;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by max on 09/06/2015.
 */
public class SearchSoundTrackAsyncTask extends AsyncTask<String, Void, Tracks> {


    private final ViewI viewI;
    private final String TAG = "SearchSoundTrackAsyncTask";

    public interface ViewI {
        void updateView(Tracks tracks);
    }

    public SearchSoundTrackAsyncTask(ViewI viewI) {
        this.viewI = viewI;
    }

    private Tracks searchTopTrack(String artist) {

        final String countryId = "FR";
        final int limit = 10;

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        final Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, 0);
        options.put(SpotifyService.LIMIT, 10);
        options.put(SpotifyService.COUNTRY, "FR");
        return spotify.getArtistTopTrack(artist, options);
    }

    @Override
    protected Tracks doInBackground(String... artist) {
        return searchTopTrack(artist[0]);
    }

    @Override
    protected void onPostExecute(Tracks tracks) {
        viewI.updateView(tracks);
    }
}