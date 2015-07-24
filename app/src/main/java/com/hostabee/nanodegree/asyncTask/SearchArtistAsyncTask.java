package com.hostabee.nanodegree.asyncTask;

import android.os.AsyncTask;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**Search Artist with Spotify api wrapper
 * Created by max on 09/06/2015.
 */
public class SearchArtistAsyncTask extends AsyncTask<String, Void, Artists> {


    private final ViewI viewI;

    public interface ViewI {
        void updateView(Artists artists);
    }

    public SearchArtistAsyncTask(ViewI viewI) {
        this.viewI = viewI;
    }

    private Artists searchArtist(String query) {

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        ArtistsPager artistsPager = spotify.searchArtists(query);

        Artists artists = new Artists();
        artists.artists = new ArrayList<>();
        artists.artists.addAll(artistsPager.artists.items);
        return  artists;
    }

    @Override
    protected Artists doInBackground(String... query) {
        return searchArtist(query[0]);
    }

    @Override
    protected void onPostExecute(Artists artists) {
        viewI.updateView(artists);
    }
}