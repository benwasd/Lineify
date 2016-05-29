package ch.bfh.ti.lineify.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.model.Track;

public class TrackRecyclerViewAdapter extends RecyclerView.Adapter<TrackRecyclerViewHolder> {
    private List<Track> tracks = null;

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        this.notifyDataSetChanged();
    }

    public Track getTrack(int position) {
        return this.tracks.get(position);
    }

    @Override
    public TrackRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = parent.inflate(parent.getContext(), R.layout.recycleritem_track, null);

        return new TrackRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrackRecyclerViewHolder viewHolder, int position) {
        if (this.tracks != null) {
            Track track = this.tracks.get(position);
            viewHolder.bindTrack(track);
        }
    }

    @Override
    public int getItemCount() {
        return this.tracks == null ? 0 : this.tracks.size();
    }
}