package ch.bfh.ti.lineify.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.DateUtil;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class WayPointRecyclerViewHolder extends RecyclerView.ViewHolder {
    private final TextView createdAtTextView;
    private final TextView altitudeTextView;
    private final TextView latitudeTextView;
    private final TextView longitudeTextView;

    public WayPointRecyclerViewHolder(View parent) {
        super(parent);
        this.createdAtTextView = (TextView) parent.findViewById(R.id.recyclerCreatedTextView);
        this.altitudeTextView = (TextView) parent.findViewById(R.id.recyclerAltitudeTextView);
        this.latitudeTextView = (TextView) parent.findViewById(R.id.recyclerLatitudeTextView);
        this.longitudeTextView = (TextView) parent.findViewById(R.id.recyclerLongitudeTextView);
    }

    public void bindWayPoint(WayPoint wayPoint) {
        this.createdAtTextView.setText(DateUtil.format(wayPoint.created()));
        this.altitudeTextView.setText(wayPoint.altitude()+"m");
        this.latitudeTextView.setText(wayPoint.latitude()+" N");
        this.longitudeTextView.setText(wayPoint.longitude()+" E");
    }
}