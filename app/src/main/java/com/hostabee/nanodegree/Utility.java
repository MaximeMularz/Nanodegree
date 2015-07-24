package com.hostabee.nanodegree;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.TimeUnit;

/**
 * Somme Useful and recurrent methods
 * Created by max on 22/06/2015.
 */
public class Utility {

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     *
     *
     * @param duration
     * @return
     */

    static public String getDurationFromMsToMinSec(long duration) {
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds( duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes(duration)));

    }

}
