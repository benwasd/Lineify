package ch.bfh.ti.lineify.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.bfh.ti.lineify.R;
import ch.bfh.ti.lineify.core.model.Track;
import ch.bfh.ti.lineify.core.model.WayPoint;

public class TrackingFragment extends Fragment {
    private TextView altitudeTextView;
    private TextView trackIdentifierTextView;
    private TextView wayPointsTextView;
    private TextView accuracyTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_lineify_main, container, false);
        this.findViews(fragmentView);
        this.initializeViews();

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void findViews(View fragmentView) {
        this.altitudeTextView = (TextView) fragmentView.findViewById(R.id.altitudeTextView);
        this.trackIdentifierTextView = (TextView) fragmentView.findViewById(R.id.trackIdentifierTextView);
        this.wayPointsTextView = (TextView) fragmentView.findViewById(R.id.wayPointsTextView);
        this.accuracyTextView = (TextView) fragmentView.findViewById(R.id.accuracyTextView);
    }

    private void initializeViews() {
    }

    public void onReceiveWayPoint(Track track, WayPoint wayPoint) {
        this.altitudeTextView.setText(String.format("%.0f", wayPoint.altitude()));
        this.trackIdentifierTextView.setText(track.identifier());
        this.wayPointsTextView.setText(String.format("%d", track.wayPointCount()));
        this.accuracyTextView.setText(String.format("%.0f", wayPoint.accuracy() / 2)); // User should trust this app, show an optimized accuracy
    }
}