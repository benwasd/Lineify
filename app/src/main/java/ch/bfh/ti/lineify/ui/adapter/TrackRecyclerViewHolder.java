package ch.bfh.ti.lineify.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.DateUtil;
import ch.bfh.ti.lineify.core.model.Track;

public class TrackRecyclerViewHolder extends RecyclerView.ViewHolder {
    private final TextView identifierTextView;
    private final TextView createdAtTextView;

    public TrackRecyclerViewHolder(View parent) {
        super(parent);
        this.identifierTextView = (TextView) parent.findViewById(R.id.identifierTextView);
        this.createdAtTextView = (TextView) parent.findViewById(R.id.createdAtTextView);
    }

    public void bindTrack(Track track) {
        this.identifierTextView.setText(track.identifier());
        this.createdAtTextView.setText(DateUtil.format(track.created()));
    }
}