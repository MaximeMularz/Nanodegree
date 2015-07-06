package com.hostabee.nanodegree.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hostabee.nanodegree.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * TracksArrayAdapter for RecyclerView in TopTenAcitivity
 * Created by max on 06/06/2015.
 */


public class TracksArrayAdapter extends RecyclerView.Adapter<TracksArrayAdapter.TracksViewHolder> {

    private final List<Track> mTracks;
    private final Context mContext;
    final private TrackAdapterOnClickHandler mClickHandler;

    public class TracksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTrackNameTextView;
        public final TextView mAlbumNameTextView;
        public final ImageView imageView;

        public TracksViewHolder(View view) {
            super(view);
            mTrackNameTextView = (TextView) view.findViewById(R.id.trackNameTextView);
            imageView = (ImageView) view.findViewById(R.id.albumPicture);
            mAlbumNameTextView = (TextView) view.findViewById(R.id.albumNameTextView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }

    public TracksArrayAdapter(Context context, TrackAdapterOnClickHandler dh, List<Track> tracks) {
        this.mContext = context;
        this.mClickHandler = dh;
        this.mTracks = tracks;
    }

    /*No implemented yet*/
    public interface TrackAdapterOnClickHandler {
        void onClick(int position);
    }

    @Override
    public TracksViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_track, viewGroup, false);
        view.setFocusable(true);
        return new TracksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TracksViewHolder tracksViewHolder, int position) {
        tracksViewHolder.mTrackNameTextView.setText(mTracks.get(position).name);
        tracksViewHolder.mAlbumNameTextView.setText(mTracks.get(position).album.name);

        //Select the low resolution
        if (mTracks.get(position).album.images.size() > 0) {
            Picasso.with(mContext).load(mTracks.get(position).album.images.get(mTracks.get(position).album.images.size() - 1).url).into(tracksViewHolder.imageView);
        }
    }

    public Track getItem(int position) {
        return mTracks.get(position);
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }
}