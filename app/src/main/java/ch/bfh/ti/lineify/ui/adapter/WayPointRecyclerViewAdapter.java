package ch.bfh.ti.lineify.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class WayPointRecyclerViewAdapter extends RecyclerView.Adapter<WayPointRecyclerViewHolder> {
    private List<WayPoint> wayPoints = null;

    public void setWayPoints(List<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
        this.notifyDataSetChanged();
    }

    public WayPoint getWayPoint(int position) {
        return this.wayPoints.get(position);
    }

    @Override
    public WayPointRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritem_waypoint, parent, false);

        return new WayPointRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WayPointRecyclerViewHolder viewHolder, int position) {
        if (this.wayPoints != null) {
            WayPoint wayPoint = this.wayPoints.get(position);
            viewHolder.bindWayPoint(wayPoint);
        }
    }

    @Override
    public int getItemCount() {
        return this.wayPoints == null ? 0 : this.wayPoints.size();
    }
}