package com.hostabee.nanodegree.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private String mTrackId = "";
    final private TrackAdapterOnClickHandler mClickHandler;

    public class TracksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTrackNameTextView;
        public final TextView mAlbumNameTextView;
        public final ImageView mImageView;
        public final CardView mCardView;


        public TracksViewHolder(View view) {
            super(view);
            view.setClickable(true);
            mTrackNameTextView = (TextView) view.findViewById(R.id.trackNameTextView);
            mImageView = (ImageView) view.findViewById(R.id.albumPicture);
            mAlbumNameTextView = (TextView) view.findViewById(R.id.albumNameTextView);
            mCardView = (CardView) view.findViewById(R.id.cardView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            if (adapterPosition >= 0) {
                mTrackId = mTracks.get(adapterPosition).id;
                mCardView.setCardBackgroundColor(Color.LTGRAY);
                mClickHandler.onClick(adapterPosition);
                notifyDataSetChanged();
            }
        }
    }


    public TracksArrayAdapter(Context context, TrackAdapterOnClickHandler dh, List<Track> tracks, String idTrack) {
        this.mContext = context;
        this.mClickHandler = dh;
        this.mTracks = tracks;
        this.mTrackId = idTrack != null ? idTrack : "";
    }

    /*No implemented yet*/
    public interface TrackAdapterOnClickHandler {
        void onClick(int position);
    }

    @Override
    public TracksViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_track, parent, false);
        view.setFocusable(true);
        return new TracksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TracksViewHolder tracksViewHolder, int position) {

        tracksViewHolder.mTrackNameTextView.setText(mTracks.get(position).name);
        tracksViewHolder.mAlbumNameTextView.setText(mTracks.get(position).album.name);

        Log.v("Postion", "mTrackId id = " + mTrackId + "mTracks.get(position).id" + mTracks.get(position).id );

        //Select the low resolution
        if (mTracks.get(position).album.images.size() > 0) {
            Picasso.with(mContext).load(mTracks.get(position).album.images.get(mTracks.get(position).album.images.size() - 1).url).into(tracksViewHolder.mImageView);
        }

        // Keep BackgroundColor
        if (mTracks.get(position).id.equals(mTrackId)) {
            tracksViewHolder.mCardView.setCardBackgroundColor(Color.LTGRAY);
        } else {
            tracksViewHolder.mCardView.setCardBackgroundColor(Color.WHITE);
        }
    }

    public Track getItem(int position) {
        return mTracks.get(position);
    }

    public void setId(String id) {
        mTrackId = id;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

}