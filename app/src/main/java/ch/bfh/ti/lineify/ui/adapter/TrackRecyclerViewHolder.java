package ch.bfh.ti.lineify.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.model.Track;

public class TrackRecyclerViewHolder extends RecyclerView.ViewHolder {
    private final TextView identifierTextView;

    public TrackRecyclerViewHolder(View parent, TextView identifierTextView) {
        super(parent);
        this.identifierTextView = identifierTextView;
    }

    public static TrackRecyclerViewHolder create(View parent) {
        TextView identifierTextView = (TextView) parent.findViewById(R.id.identifierTextView);

        return new TrackRecyclerViewHolder(parent, identifierTextView);
    }

    public void bindTrack(Track track) {
        this.identifierTextView.setText(track.identifier());
    }
}