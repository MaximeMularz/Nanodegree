package com.hostabee.nanodegree.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hostabee.nanodegree.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * ArtistArrayAdapter used by ListView in SearchForAnActivity
 * Created by max on 06/06/2015.
 */


public class ArtistArrayAdapter extends ArrayAdapter<Artist> {

    private final List<Artist> artists;
    private final Context context;

    public ArtistArrayAdapter(Context context,List<Artist> artists) {
        super(context, R.layout.row_artist, artists);
        this.context = context;
        this.artists = artists;
    }

    static class ViewHolder {
        TextView text;
        ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_artist, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            view.setTag(viewHolder);
            viewHolder.text = (TextView) view.findViewById(R.id.artistName);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.albumPicture);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        //check if Artist has a picture
        if (artists.get(position).images.size() > 0) {
            Picasso.with(context).load(artists.get(position).images.get(0).url).into(holder.imageView);
        }
        holder.text.setText(artists.get(position).name);

        return view;
    }

}